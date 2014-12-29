package com.ss.context;

import java.util.Map;

import com.ss.beans.BeanDefinition;
import com.ss.beans.factory.AbstractBeanFactory;
import com.ss.beans.factory.AutowireCapableBeanFactory;
import com.ss.beans.factory.xml.XmlBeanDefinitionReader;
import com.ss.core.io.ResourceLoader;

public class ClassPathXmlApplicationContext extends AbstractorApplicationContext {

	private String configLocation;

	public ClassPathXmlApplicationContext(String configLocation)throws Exception {
		this(configLocation, new AutowireCapableBeanFactory());
	}

	public ClassPathXmlApplicationContext(String configLocation,AbstractBeanFactory beanFactory) throws Exception {
		super(beanFactory);
		this.configLocation = configLocation;
		refresh();
	}

	@Override
	protected void loadBeanDefinitions(AbstractBeanFactory beanFactory)throws Exception {
		XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
		xmlBeanDefinitionReader.loadBeanDefinitions(configLocation);
		for (Map.Entry<String, BeanDefinition>  beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
			beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(),beanDefinitionEntry.getValue());
		}
	}
}
