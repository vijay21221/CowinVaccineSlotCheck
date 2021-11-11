package com.example
/**
 * To get beans in classes which has no context
 *
 * @author Prasad
 */
class BeanFactory {

    static Object getBean(Class beanClass){
        return Application.applicationContext.getBean(beanClass)
    }

    static Object getBean(Class beanClass, Object... args){
        return Application.applicationContext.createBean(beanClass, args)
    }
}
