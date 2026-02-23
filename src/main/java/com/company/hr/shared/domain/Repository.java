package com.company.hr.shared.domain;

import java.util.Optional;

/**
 * 仓储接口
 * DDD中的Repository模式
 */
public interface Repository<T extends AggregateRoot<ID>, ID> {
    
    /**
     * 保存聚合根
     */
    T save(T aggregate);
    
    /**
     * 根据ID查找聚合根
     */
    Optional<T> findById(ID id);
    
    /**
     * 删除聚合根
     */
    void delete(T aggregate);
    
    /**
     * 根据ID删除聚合根
     */
    void deleteById(ID id);
    
    /**
     * 判断聚合根是否存在
     */
    boolean existsById(ID id);
}

