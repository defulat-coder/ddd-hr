package com.company.hr.shared.domain;

/**
 * 值对象标记接口
 * DDD中的值对象，通过属性值来判断相等性
 */
public interface ValueObject {
    // 值对象标记接口
    // 实现类应该是不可变的，并重写equals和hashCode
}

