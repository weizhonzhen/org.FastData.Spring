package org.FastData.Spring.FastServiceAop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class BeforeContext {
   private Method method;
    private Object[] args;
    private Object Result;
    private  boolean IsReturn;
    private Annotation[] annotations;

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }

    public boolean isReturn() {
        return IsReturn;
    }

    public void setReturn(boolean aReturn) {
        IsReturn = aReturn;
    }

    public Object getResult() {
        return Result;
    }

    public void setResult(Object result) {
        Result = result;
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
