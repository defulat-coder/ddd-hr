package com.company.hr.shared.acl;

/**
 * 翻译器接口
 * 用于在不同上下文之间进行模型转换
 */
public interface Translator<SOURCE, TARGET> {
    
    /**
     * 将源模型翻译为目标模型
     */
    TARGET translate(SOURCE source);
}

