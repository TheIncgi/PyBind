/**
 * 
 */
package com.theincgi.pyBind;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.theincgi.pyBind.PyBindSockerHandler.ResultMode;

@Documented
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface Py {
	String lib();
	String name();
	ResultMode mode() default ResultMode.COPY;
	Class[] listClasses() default {};
}
