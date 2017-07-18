package com.pugwoo.wooutils.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁，注解在方法上。 暂时不支持还按照方法的参数来独立限制。
 * 
 * 关于返回值：如果方法是常规返回值，当没有获取到锁时，且超过了阻塞等待时间，则返回null
 * @author nick markfly
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Synchronized {

	/**
	 * 分布式锁命名空间，每个业务有自己唯一的分布式锁namespace，相同的namespace表示同一把锁。
	 * @return
	 */
	String namespace();
	
	/**
	 * 事务超时的秒数，如果使用者超过这个时间还没有主动释放锁，那么redis会自动释放掉该锁。
	 * 请使用者合理评估任务执行时间，推荐按正常执行时间的10倍~100倍评估该时间。
	 * @return
	 */
	int expireSecond() default 60;
	
	
}
