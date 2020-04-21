package jk.util.retrial.core;

import java.io.IOException;

public class RetrialUtils {

    public static <T extends RetriableService> T createRetryableProxy(Class<?> interfaceClass,RetriableService.RetryContext context, Object instance)
            throws IOException {
        T retriableService;
        if (instance instanceof AbtractRetriableService) {
            retriableService = (T) instance;
            retriableService.initialise(context);
            retriableService = (T) RetriableServiceProxyProvider.getRetriableServiceProxy(interfaceClass,
                    retriableService);
            return  retriableService;
        }
        return null;
    }

    public static <T extends RetriableService> T createRetryableProxy(Class<?> interfaceClass,RetriableService.RetryContext context, Object instance,Class<? extends Exception> exception)
            throws IOException {
        T retriableService;
        if (instance instanceof AbtractRetriableService) {
            retriableService = (T) instance;
            retriableService.initialise(context);
            retriableService = (T) RetriableServiceProxyProvider.getRetriableServiceProxy(interfaceClass,
                    retriableService,exception);
            return retriableService;
        }
        return null;
    }
}
