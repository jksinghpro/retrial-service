package io.github.jksinghpro.retrial.example;

import io.github.jksinghpro.retrial.core.RetriableService;
import io.github.jksinghpro.retrial.core.RetrialUtils;


public class Main {

    public static void main(String[] args) throws Exception {
        Service service = (Service) RetrialUtils.createRetryableProxy(Service.class,new RetriableService.RetryContext(3,1000),new SampleService());
        service.doThis("hello");

    }
}
