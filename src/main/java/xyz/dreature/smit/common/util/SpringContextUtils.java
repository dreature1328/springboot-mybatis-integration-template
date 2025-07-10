package xyz.dreature.smit.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

// 该工具类是固定写法，用于让其它普通类可以调用Service层的服务
public class SpringContextUtils implements ApplicationContextAware {
    // 在普通类可以通过调用 SpringUtils.getAppContext() 获取 applicationContext 对象
    private static ApplicationContext applicationContext = null;

    // 获取 applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringContextUtils.applicationContext == null) {
            SpringContextUtils.applicationContext = applicationContext;
        }
//        System.out.println("---------------------------------------------------------------------");
//        System.out.println("======== ApplicationContext 配置成功, applicationContext = " + SpringContextUtils.applicationContext + " ========");
    }

    // 通过 name 获取 Bean
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    // 通过 class 获取 Bean
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    // 通过 name 以及 class 获取 Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }


}

