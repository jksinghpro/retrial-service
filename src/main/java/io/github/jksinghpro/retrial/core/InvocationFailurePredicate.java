package io.github.jksinghpro.retrial.core;


import net.jodah.failsafe.function.BiPredicate;

import java.lang.reflect.InvocationTargetException;


public class InvocationFailurePredicate implements BiPredicate<Object,Throwable> {

    Class<? extends Throwable> retryOn;


    public InvocationFailurePredicate(Class<? extends Throwable> retryOn) {
        this.retryOn = retryOn;
    }

    @Override
    public boolean test(Object o, Throwable throwable) {
        if(throwable == null){
            return false;
        }
        if(throwable instanceof InvocationTargetException){
            throwable  = throwable.getCause();
            if(throwable.getClass().isAssignableFrom(retryOn)){
                return true;
            }
        }else {
            if(throwable.getClass().isAssignableFrom(retryOn)){
                return true;
            }
        }
        return false;
    }

}
