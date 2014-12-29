package com.ss.beans;
/**
 * 用于属性注入
 * @author zhangxuewen
 *
 * @date  下午02:01:59
 */
public class PropertyValue {
	 private final String name;
	 private final Object value;
	 public PropertyValue(String name, Object value) {
		 this.name = name;
		 this.value = value;
	 }
	public String getName() {
		return name;
	}
	public Object getValue() {
		return value;
	}
    
}
