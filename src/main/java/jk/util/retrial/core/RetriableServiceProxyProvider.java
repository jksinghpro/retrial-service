package jk.util.retrial.core;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.function.BiPredicate;
import net.jodah.failsafe.function.CheckedConsumer;
import net.jodah.failsafe.function.CheckedRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.http.HTTPException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Generic class which returns a proxy object for an instance with retriable
 * mechanism implemented
 */
public class RetriableServiceProxyProvider {

  /**
   *
   * @param serviceInterface
   * @param serviceImplementation
   * @param <T>
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T extends RetriableService> T getRetriableServiceProxy(Class<?> serviceInterface,
      T serviceImplementation) {
    RetriableServiceProxyInvocationHandler invocationHandler = new RetriableServiceProxyInvocationHandler(
        serviceImplementation);
    return  (T) Proxy.newProxyInstance(serviceImplementation.getClass().getClassLoader(),
        new Class[] { serviceInterface , RetriableService.class}, invocationHandler);
  }

  @SuppressWarnings("unchecked")
  public static <T extends RetriableService> T getRetriableServiceProxy(Class<?> serviceInterface,
                                                                        T serviceImplementation,Class<? extends Exception> retryException) {
    RetriableServiceProxyInvocationHandler invocationHandler = new RetriableServiceProxyInvocationHandler(
            serviceImplementation);
    return (T) Proxy.newProxyInstance(serviceImplementation.getClass().getClassLoader(),
            new Class[] { serviceInterface }, invocationHandler);
  }

  /**
   * Generic service class implementing invocation handler
   */
  private static class RetriableServiceProxyInvocationHandler implements InvocationHandler {

    /**
     * Indicates establishment of connection of service
     */
    private boolean connectionFlag = false;

    /**
     * Retriable service
     */
    private RetriableService service;

    /**
     * Logger instance
     */
    private Logger logger;

    /**
     * Retry policy instance
     */
    private RetryPolicy retryPolicy;

    /**
     * Constrcutor
     *
     * @param service
     */
    public RetriableServiceProxyInvocationHandler(RetriableService service) {
      this.service = service;
      this.logger = LoggerFactory.getLogger(service.getClass());
      this.retryPolicy = new RetryPolicy().retryOn(HTTPException.class)
          .withDelay(service.getRetryWait(), TimeUnit.MILLISECONDS).withMaxRetries(service.getRetryCount());
    }


    /**
     * Constrcutor
     *
     * @param service
     */
    public RetriableServiceProxyInvocationHandler(RetriableService service,Class<? extends Exception> retryException) {
      this.service = service;
      this.logger = LoggerFactory.getLogger(service.getClass());
      this.retryPolicy = new RetryPolicy().retryOn(retryException)
              .withDelay(service.getRetryWait(), TimeUnit.MILLISECONDS).withMaxRetries(service.getRetryCount());
    }

    /**
     * Return value class
     */
    class ReturnValue {
      Object value;
    }

    /**
     *
     * @param proxy
     *          Proxy object
     * @param method
     *          Method invoked
     * @param args
     *          args passed
     * @return Returns result evaluated
     * @throws Throwable
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

      if (!connectionFlag) {
        Failsafe.with(retryPolicy).run(new CheckedRunnable() {
          @Override
          public void run() throws Exception {
            service.connect();
            connectionFlag = true;
          }
        });
      }
      final ReturnValue returnValue = new ReturnValue();
      if (method.isAnnotationPresent(RetriableMethod.class)) {
          RetriableMethod retriableMethod = method.getAnnotation(RetriableMethod.class);
          BiPredicate<Object,Throwable> predicate =new InvocationFailurePredicate(retriableMethod.retryOn());
          this.retryPolicy = new RetryPolicy().retryIf(predicate)
                  .withDelay(service.getRetryWait(), TimeUnit.MILLISECONDS).withMaxRetries(service.getRetryCount());
          List<Method> methods = Arrays.asList(service.getClass().getMethods());
          methods = methods.stream().filter(method1 -> method1.isAnnotationPresent(RetryWith.class)).collect(Collectors.toList());
          methods = methods.stream().filter(method1 -> method1.getAnnotation(RetryWith.class).alias().equalsIgnoreCase(retriableMethod.retryWith())).collect(Collectors.toList());
          List<Method> finalMethods = methods;
          Failsafe.with(retryPolicy).onRetry(   new CheckedConsumer<Exception>() {
          @Override
          public void accept(Exception exception) throws Exception {
            logger.info("Failed to call method  {} on service {} ", method, service);
            logger.info("Remote service method call failed.", exception);
            if(finalMethods.size() > 0 ) {
                finalMethods.get(0).invoke(service);
            }
          }
        }).run(new CheckedRunnable() {
          @Override
          public void run() throws Exception {
            returnValue.value = method.invoke(service, args);
          }
        });
      }
      return returnValue.value;
    }

  }

}
