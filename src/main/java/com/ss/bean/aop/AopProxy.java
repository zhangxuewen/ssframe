package com.ss.bean.aop;

public interface AopProxy {
	Object getProxy();
	Object getProxy(ClassLoader classLoader);
}
