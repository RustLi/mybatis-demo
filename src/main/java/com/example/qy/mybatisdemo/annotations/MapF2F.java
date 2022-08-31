package com.example.qy.mybatisdemo.annotations;

import java.lang.annotation.*;

/**
 * @title MapF2F
 * @desc 将查询结果映射成map的注解，其中第一个字段为key，第二个字段为velue</br>
 *       注：返回类型必须为（{@link java.util.Map Map<K,V>}）,K/V的类型通过 mybatis的TypeHander进行类型转换，如意欧必要可自定义TypeHander
 * @author shanyu
 * @date 2018年7月27日
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MapF2F {
    /**
     * 是否允许key重复。如果不允许，而实际结果出现了重复，会抛出org.springframework.dao.DuplicateKeyException。
     * 
     * @return
     */
    boolean isAllowKeyRepeat() default true;

    /**
     * 对于相同的key，是否允许value不同(在允许key重复的前提下)。如果允许，则按查询结果，后面的覆盖前面的；如果不允许，则会抛出org.springframework.dao.DuplicateKeyException。
     * 
     * @return
     */
    boolean isAllowValueDifferentWithSameKey() default false;
}
