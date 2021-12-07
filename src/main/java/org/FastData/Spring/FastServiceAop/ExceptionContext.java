package org.FastData.Spring.FastServiceAop;

import java.lang.reflect.Method;

public class ExceptionContext {
    private Exception exception;
    private Method method;
    private Object[] args;

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
