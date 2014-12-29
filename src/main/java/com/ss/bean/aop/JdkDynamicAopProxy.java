package com.ss.bean.aop;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JdkDynamicAopProxy implements AopProxy, InvocationHandler,Serializable {


	private static final long serialVersionUID = 5531744639992436476L;

	/** We use a static Log to avoid serialization issues */

	/** Config used to configure this proxy */
	private final AdvisedSupport advised;
	/**
	 * Is the {@link #equals} method defined on the proxied interfaces?
	 */
	private boolean equalsDefined;
	/**
	 * Is the {@link #hashCode} method defined on the proxied interfaces?
	 */
	private boolean hashCodeDefined;

	/**
	 * Construct a new JdkDynamicAopProxy for the given AOP configuration.
	 * 
	 * @param config
	 *            the AOP configuration as AdvisedSupport object
	 * @throws AopConfigException
	 *             if the config is invalid. We try to throw an informative
	 *             exception in this case, rather than let a mysterious failure
	 *             happen later.
	 */
	public JdkDynamicAopProxy(AdvisedSupport config) throws AopConfigException {
		Assert.notNull(config, "AdvisedSupport must not be null");
		if (config.getAdvisors().length == 0
				&& config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE) {
			throw new AopConfigException(
					"No advisors and no TargetSource specified");
		}
		this.advised = config;
	}

	@Override
	public Object getProxy() {
		return getProxy(ClassUtils.getDefaultClassLoader());
	}

	@Override
	public Object getProxy(ClassLoader classLoader) {
		if (logger.isDebugEnabled()) {
			logger.debug("Creating JDK dynamic proxy: target source is "
					+ this.advised.getTargetSource());
		}
		Class<?>[] proxiedInterfaces = AopProxyUtils
				.completeProxiedInterfaces(this.advised);
		findDefinedEqualsAndHashCodeMethods(proxiedInterfaces);
		return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
	}

	/**
	 * Finds any {@link #equals} or {@link #hashCode} method that may be defined
	 * on the supplied set of interfaces.
	 * 
	 * @param proxiedInterfaces
	 *            the interfaces to introspect
	 */
	private void findDefinedEqualsAndHashCodeMethods(
			Class<?>[] proxiedInterfaces) {
		for (Class<?> proxiedInterface : proxiedInterfaces) {
			Method[] methods = proxiedInterface.getDeclaredMethods();
			for (Method method : methods) {
				if (AopUtils.isEqualsMethod(method)) {
					this.equalsDefined = true;
				}
				if (AopUtils.isHashCodeMethod(method)) {
					this.hashCodeDefined = true;
				}
				if (this.equalsDefined && this.hashCodeDefined) {
					return;
				}
			}
		}
	}

	/**
	 * Implementation of {@code InvocationHandler.invoke}.
	 * <p>
	 * Callers will see exactly the exception thrown by the target, unless a
	 * hook method throws an exception.
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		MethodInvocation invocation;
		Object oldProxy = null;
		boolean setProxyContext = false;
		TargetSource targetSource = this.advised.targetSource;
		Class<?> targetClass = null;
		Object target = null;
		try {
			if (!this.equalsDefined && AopUtils.isEqualsMethod(method)) {
				// The target does not implement the equals(Object) method
				// itself.
				return equals(args[0]);
			}
			if (!this.hashCodeDefined && AopUtils.isHashCodeMethod(method)) {
				// The target does not implement the hashCode() method itself.
				return hashCode();
			}
			if (!this.advised.opaque
					&& method.getDeclaringClass().isInterface()
					&& method.getDeclaringClass().isAssignableFrom(
							Advised.class)) {
				// Service invocations on ProxyConfig with the proxy config...
				return AopUtils.invokeJoinpointUsingReflection(this.advised,
						method, args);
			}
			Object retVal;
			if (this.advised.exposeProxy) {
				// Make invocation available if necessary.
				oldProxy = AopContext.setCurrentProxy(proxy);
				setProxyContext = true;
			}
			// May be null. Get as late as possible to minimize the time we
			// "own" the target,
			// in case it comes from a pool.
			target = targetSource.getTarget();
			if (target != null) {
				targetClass = target.getClass();
			}
			// Get the interception chain for this method.
			List<Object> chain = this.advised
					.getInterceptorsAndDynamicInterceptionAdvice(method,
							targetClass);
			// Check whether we have any advice. If we don't, we can fallback on
			// direct
			// reflective invocation of the target, and avoid creating a
			// MethodInvocation.
			if (chain.isEmpty()) {
				// We can skip creating a MethodInvocation: just invoke the
				// target directly
				// Note that the final invoker must be an InvokerInterceptor so
				// we know it does
				// nothing but a reflective operation on the target, and no hot
				// swapping or fancy proxying.
				retVal = AopUtils.invokeJoinpointUsingReflection(target,
						method, args);
			} else {
				// We need to create a method invocation...
				invocation = new ReflectiveMethodInvocation(proxy, target,
						method, args, targetClass, chain);
				// Proceed to the joinpoint through the interceptor chain.
				retVal = invocation.proceed();
			}
			// Massage return value if necessary.
			Class<?> returnType = method.getReturnType();
			if (retVal != null
					&& retVal == target
					&& returnType.isInstance(proxy)
					&& !RawTargetAccess.class.isAssignableFrom(method
							.getDeclaringClass())) {
				// Special case: it returned "this" and the return type of the
				// method
				// is type-compatible. Note that we can't help if the target
				// sets
				// a reference to itself in another returned object.
				retVal = proxy;
			} else if (retVal == null && returnType != Void.TYPE
					&& returnType.isPrimitive()) {
				throw new AopInvocationException(
						"Null return value from advice does not match primitive return type for: "
								+ method);
			}
			return retVal;
		} finally {
			if (target != null && !targetSource.isStatic()) {
				// Must have come from TargetSource.
				targetSource.releaseTarget(target);
			}
			if (setProxyContext) {
				// Restore old proxy.
				AopContext.setCurrentProxy(oldProxy);
			}
		}
	}

	/**
	 * Equality means interfaces, advisors and TargetSource are equal.
	 * <p>
	 * The compared object may be a JdkDynamicAopProxy instance itself or a
	 * dynamic proxy wrapping a JdkDynamicAopProxy instance.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (other == null) {
			return false;
		}
		JdkDynamicAopProxy otherProxy;
		if (other instanceof JdkDynamicAopProxy) {
			otherProxy = (JdkDynamicAopProxy) other;
		} else if (Proxy.isProxyClass(other.getClass())) {
			InvocationHandler ih = Proxy.getInvocationHandler(other);
			if (!(ih instanceof JdkDynamicAopProxy)) {
				return false;
			}
			otherProxy = (JdkDynamicAopProxy) ih;
		} else {
			// Not a valid comparison...
			return false;
		}
		// If we get here, otherProxy is the other AopProxy.
		return AopProxyUtils.equalsInProxy(this.advised, otherProxy.advised);
	}

	/**
	 * Proxy uses the hash code of the TargetSource.
	 */
	@Override
	public int hashCode() {
		return JdkDynamicAopProxy.class.hashCode() * 13
				+ this.advised.getTargetSource().hashCode();
	}
}
