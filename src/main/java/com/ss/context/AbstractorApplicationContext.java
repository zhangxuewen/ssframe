package com.ss.context;

import java.util.List;

import com.ss.beans.BeanProcessor;
import com.ss.beans.factory.AbstractBeanFactory;

public abstract class AbstractorApplicationContext implements
		ApplicationContext {
	protected AbstractBeanFactory beanFactory;

	public AbstractorApplicationContext(AbstractBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public Object getBean(String name) throws Exception {
		return null;
	}

	public void refresh() throws Exception {
		loadBeanDefinitions(beanFactory);
		registerBeanPostProcessors(beanFactory);
		onRefresh();
	}

	protected abstract void loadBeanDefinitions(AbstractBeanFactory beanFactory)
			throws Exception;

	protected void registerBeanPostProcessors(AbstractBeanFactory beanFactory)
			throws Exception {
		List beanPostProcessors = beanFactory
				.getBeansForType(BeanProcessor.class);
		for (Object beanPostProcessor : beanPostProcessors) {
			beanFactory.addBeanProcessor(((BeanProcessor) beanPostProcessor));
		}
	}

	protected void onRefresh() throws Exception {
		beanFactory.preInstantiateSingletons();
	}
}
