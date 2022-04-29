package org.FastData.Spring.FastServiceAop;

import org.FastData.Spring.Util.CacheUtil;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class FastServiceProxy {
    private ConfigurableListableBeanFactory beanFactory = CacheUtil.getModel("beanFactory", ConfigurableListableBeanFactory.class);
    private IFastServiceAop iFastServiceAop = CacheUtil.getModel("FastServiceAop", IFastServiceAop.class);
    private Object object;
    private Class<?> aClass;

    public Object invoke(Class<?> object) {
        try {
            boolean isBean = object.getAnnotations().length > 0;
            if (isBean)
                this.object = beanFactory.getBean(object);
            else
                this.object = object.newInstance();

            this.aClass = object;
            Class<?> interfaces = object.getInterfaces()[0];
            return Proxy.newProxyInstance(this.object.getClass().getClassLoader(), new Class[]{interfaces}, new AopHandle());
        } catch (Exception ex) {
            ExceptionContext exception = new ExceptionContext();
            exception.setException(ex);
            iFastServiceAop.exception(exception);
            return exception.getResult();
        }
    }

    class AopHandle implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws NoSuchMethodException {
            Method temp = aClass.getMethod(method.getName(),method.getParameterTypes());
            try {
                BeforeContext before = new BeforeContext();
                before.setArgs(args);
                before.setAnnotations(temp.getAnnotations());
                before.setMethod(temp);
                iFastServiceAop.before(before);

                if(before.isReturn())
                    return before.getResult();

                Object ret = method.invoke(object, args);

                AfterContext after = new AfterContext();
                after.setAnnotations(temp.getAnnotations());
                after.setArgs(args);
                after.setMethod(temp);
                after.setResult(ret);
                iFastServiceAop.after(after);

                return after.getResult();
            } catch (Exception ex) {
                ExceptionContext exception = new ExceptionContext();
                exception.setException(ex);
                exception.setArgs(args);
                exception.setAnnotations(temp.getAnnotations());
                exception.setMethod(temp);
                iFastServiceAop.exception(exception);

                if(exception.isReturn())
                    return exception.getResult();
                else
                 return null;
            }
        }
    }
}
