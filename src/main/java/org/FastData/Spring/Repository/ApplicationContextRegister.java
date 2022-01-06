package org.FastData.Spring.Repository;

import org.FastData.Spring.FastServiceAop.FastServiceProxy;
import org.FastData.Spring.Util.CacheUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextRegister implements BeanFactoryPostProcessor {
    private static ConfigurableListableBeanFactory beanFactory;

    public static void setBean(Class<?> type) {
        if (type != null && beanFactory != null) {
            FastProxy fastProxy = new FastProxy(null);
            beanFactory.registerSingleton(type.getName(), type.cast(fastProxy.instance(type)));
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ApplicationContextRegister.beanFactory = beanFactory;
        CacheUtil.setModel("beanFactory", beanFactory);
    }
}
