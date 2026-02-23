package com.company.hr.shared.acl;

/**
 * 外部系统适配器接口
 * 防腐层的核心接口，用于适配外部系统
 */
public interface ExternalSystemAdapter<T, R> {
    
    /**
     * 将外部模型转换为领域模型
     */
    T toDomainModel(R externalModel);
    
    /**
     * 将领域模型转换为外部模型
     */
    R toExternalModel(T domainModel);
}

