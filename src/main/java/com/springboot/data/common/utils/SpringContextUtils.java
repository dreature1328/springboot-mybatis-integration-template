package com.springboot.data.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Description: 该工具类是固定写法，用于让其它普通类可以调用Service层的服务
 */
public class SpringContextUtils implements ApplicationContextAware {
    // 在普通类可以通过调用 SpringUtils.getAppContext() 获取 applicationContext 对象
    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringContextUtils.applicationContext == null){
            SpringContextUtils.applicationContext  = applicationContext;
        }
//        System.out.println("---------------------------------------------------------------------");
//        System.out.println("========ApplicationContext配置成功,applicationContext="+SpringContextUtils.applicationContext+"========");
    }

    /**
     * @description 获取applicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * @description 通过name获取 Bean
     */
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    /**
     * @description 通过class获取Bean
     */
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    /**
     * @description 通过name,以及Clazz返回指定的Bean
     */
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }


}

