package com.ss.beans;

import java.util.HashMap;
import java.util.Map;

import com.ss.core.io.ResourceLoader;

public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {
	private Map<String, BeanDefinition> registry;
	private ResourceLoader resourceLoader;

	protected AbstractBeanDefinitionReader(ResourceLoader resourceLoader) {
		this.registry = new HashMap<String, BeanDefinition>();
		this.resourceLoader = resourceLoader;
	}

	public Map<String, BeanDefinition> getRegistry() {
		return registry;
	}

	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public void loadBeanDefinitions(String location) throws Exception {
		// TODO Auto-generated method stub

	}
}
