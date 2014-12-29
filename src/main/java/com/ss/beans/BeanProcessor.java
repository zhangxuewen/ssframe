package com.ss.beans;

public interface BeanProcessor {
	Object postProcessBeforeInitialization(Object bean, String beanName)throws Exception;

	Object postProcessAfterInitialization(Object bean, String beanName)throws Exception;
}
