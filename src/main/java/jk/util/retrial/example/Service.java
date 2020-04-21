package jk.util.retrial.example;

import jk.util.retrial.core.RetriableMethod;

import java.io.IOException;


public interface Service {

    @RetriableMethod(retryOn = IOException.class,retryWith = "connect")
    public void doThis(String input) throws Exception;

    public void doThat(String input);

}
