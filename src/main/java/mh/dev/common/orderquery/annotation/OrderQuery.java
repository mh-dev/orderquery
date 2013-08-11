package mh.dev.common.orderquery.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mh.dev.common.orderquery.Order;

@Target(value = { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface OrderQuery {

	String name();

	String query();

	String defaultColumn() default "";

	Order defaultOrder() default Order.NONE;

	OrderQueryColumn[] orderQueryColumns() default {};

}
