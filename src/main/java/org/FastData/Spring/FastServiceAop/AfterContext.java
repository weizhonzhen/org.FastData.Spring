package org.FastData.Spring.FastServiceAop;

import java.lang.reflect.Method;

public class AfterContext {
    private Method method;
    private Object[] args;
    private Object result;

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

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
