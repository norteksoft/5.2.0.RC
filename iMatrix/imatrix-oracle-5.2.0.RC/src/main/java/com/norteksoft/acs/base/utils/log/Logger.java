package com.norteksoft.acs.base.utils.log;
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.METHOD,java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Logger {
	  public abstract String key() default "";
}
