package io.github.jksinghpro.retrial.example;


import io.github.jksinghpro.retrial.core.RetriableMethod;
import io.github.jksinghpro.retrial.core.RetryWith;
import io.github.jksinghpro.retrial.core.AbtractRetriableService;

import java.io.File;
import java.io.IOException;

public class SampleService extends AbtractRetriableService implements Service {

    @RetryWith(alias = "connect")
    @Override
    public void connect() throws IOException {
        System.out.println("Connected Succesfully");
    }

    public void doThis(String input) throws Exception {
        System.out.println("Done that");
        System.out.println(input);
        File file = new File("happy");
        if(!file.exists()) {
            throw new IOException();
        }
        System.out.println("execution complete");
    }

    @RetriableMethod(retryOn = NullPointerException.class,retryWith = "connect")
    public void doThat(String input) {
        System.out.println("Done that");
        System.out.println(input);
        throw new NullPointerException();
    }
}
