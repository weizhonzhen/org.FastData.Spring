package org.FastData.Spring.FastServiceAop;

import org.FastData.Spring.Util.CacheUtil;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class FastServiceProxy {
    private ConfigurableListableBeanFactory beanFactory = CacheUtil.getModel("beanFactory", ConfigurableListableBeanFactory.class);
    private IFastServiceAop iFastServiceAop = CacheUtil.getModel("FastServiceAop", IFastServiceAop.class);
    private Object object;

    public Object invoke(Class<?> object) {
        try {
            boolean isBean = object.getAnnotations().length > 0;
            if (isBean)
                this.object = beanFactory.getBean(object);
            else
                this.object = object.newInstance();

            Class<?> interfaces = object.getInterfaces()[0];
            return Proxy.newProxyInstance(this.object.getClass().getClassLoader(), new Class[]{interfaces}, new AopHandle());
        } catch (Exception ex) {
            ExceptionContext exception = new ExceptionContext();
            exception.setException(ex);
            iFastServiceAop.exception(exception);
            return null;
        }
    }

    class AopHandle implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            try {
                BeforeContext before = new BeforeContext();
                before.setArgs(args);
                before.setMethod(method);
                iFastServiceAop.before(before);

                Object ret = method.invoke(object, args);

                AfterContext after = new AfterContext();
                after.setArgs(args);
                after.setMethod(method);
                after.setResult(ret);
                iFastServiceAop.after(after);

                return ret;
            } catch (Exception ex) {
                ExceptionContext exception = new ExceptionContext();
                exception.setException(ex);
                exception.setArgs(args);
                exception.setMethod(method);
                iFastServiceAop.exception(exception);
                return null;
            }
        }
    }
}
