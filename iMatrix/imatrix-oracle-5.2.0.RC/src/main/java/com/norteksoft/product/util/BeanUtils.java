package com.norteksoft.product.util;

import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * 反射的Utils函数集合.
 * 
 * 提供侵犯隐私的直接读取filed的能力.
 */
public class BeanUtils {

	protected static Log logger = LogFactory.getLog(BeanUtils.class);

	private BeanUtils() {
	}

	/**
	 * 直接读取对象属性值,无视private/protected修饰符,不经过getter函数.
	 */
	public static Object getFieldValue(Object object, String fieldName) throws NoSuchFieldException {
		Field field = getDeclaredField(object, fieldName);
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}

		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalAccessException e) {
			logger.error("不可能抛出的异常", e);
		}
		return result;
	}

	/**
	 * 直接设置对象属性值,无视private/protected修饰符,不经过setter函数.
	 */
	public static void setFieldValue(Object object, String fieldName, Object value) throws NoSuchFieldException {
		Field field = getDeclaredField(object, fieldName);
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			logger.error("不可能抛出的异常", e);
		}
	}

	/**
	 * 循环向上转型,获取对象的DeclaredField.
	 */
	public static Field getDeclaredField(Object object, String fieldName) throws NoSuchFieldException {
		Assert.notNull(object);
		return getDeclaredField(object.getClass(), fieldName);
	}

	/**
	 * 循环向上转型,获取类的DeclaredField.
	 */
	@SuppressWarnings("unchecked")
	public static Field getDeclaredField(Class clazz, String fieldName) throws NoSuchFieldException {
		Assert.notNull(clazz);
		Assert.hasText(fieldName);
		for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				// Field不在当前类定义,继续向上转型
			}
		}
		throw new NoSuchFieldException("No such field: " + clazz.getName() + '.' + fieldName);
	}
}
