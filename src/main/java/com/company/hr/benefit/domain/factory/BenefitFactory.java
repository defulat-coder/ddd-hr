package com.company.hr.benefit.domain.factory;

import com.company.hr.benefit.domain.model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 福利工厂
 * 负责创建福利聚合根
 */
@Component
public class BenefitFactory {
    
    /**
     * 创建标准福利
     * 
     * @param name 福利名称
     * @param description 描述
     * @param type 福利类型
     * @param cost 福利成本
     * @param eligibilityCriteria 资格标准
     * @return 新创建的福利
     */
    public Benefit createBenefit(
            String name,
            String description,
            BenefitType type,
            BenefitCost cost,
            String eligibilityCriteria) {
        
        BenefitId benefitId = BenefitId.generate();
        
        return new Benefit(
            benefitId,
            name,
            description,
            type,
            cost,
            eligibilityCriteria
        );
    }
    
    /**
     * 创建基础社保福利（五险）
     * 
     * @return 社保福利
     */
    public Benefit createSocialInsurance() {
        BenefitCost cost = new BenefitCost(
            new BigDecimal("1500"), // 企业承担
            new BigDecimal("500")   // 员工承担
        );
        
        return createBenefit(
            "五险",
            "养老保险、医疗保险、失业保险、工伤保险、生育保险",
            BenefitType.HEALTH_INSURANCE,
            cost,
            "所有正式员工"
        );
    }
    
    /**
     * 创建住房公积金
     * 
     * @return 公积金福利
     */
    public Benefit createHousingFund() {
        BenefitCost cost = new BenefitCost(
            new BigDecimal("1200"), // 企业承担
            new BigDecimal("1200")  // 员工承担
        );
        
        return createBenefit(
            "住房公积金",
            "住房公积金，可用于购房、租房",
            BenefitType.HOUSING_ALLOWANCE,
            cost,
            "所有正式员工"
        );
    }
    
    /**
     * 创建餐补福利
     * 
     * @param monthlyAmount 每月金额
     * @return 餐补福利
     */
    public Benefit createMealAllowance(BigDecimal monthlyAmount) {
        BenefitCost cost = new BenefitCost(
            monthlyAmount,          // 企业全额承担
            BigDecimal.ZERO
        );
        
        return createBenefit(
            "餐补",
            String.format("每月餐补%s元", monthlyAmount),
            BenefitType.MEAL_ALLOWANCE,
            cost,
            "所有在职员工（含试用期）"
        );
    }
    
    /**
     * 创建交通补贴
     * 
     * @param monthlyAmount 每月金额
     * @return 交通补贴福利
     */
    public Benefit createTransportAllowance(BigDecimal monthlyAmount) {
        BenefitCost cost = new BenefitCost(
            monthlyAmount,
            BigDecimal.ZERO
        );
        
        return createBenefit(
            "交通补贴",
            String.format("每月交通补贴%s元", monthlyAmount),
            BenefitType.TRANSPORT_ALLOWANCE,
            cost,
            "所有正式员工"
        );
    }
    
    /**
     * 创建补充医疗保险
     * 
     * @return 补充医疗保险
     */
    public Benefit createSupplementaryHealthInsurance() {
        BenefitCost cost = new BenefitCost(
            new BigDecimal("500"),  // 企业承担
            BigDecimal.ZERO
        );
        
        return createBenefit(
            "补充医疗保险",
            "覆盖社保外的医疗费用，年度额度10万元",
            BenefitType.HEALTH_INSURANCE,
            cost,
            "入职满1年的正式员工"
        );
    }
    
    /**
     * 创建健身房会员福利
     * 
     * @return 健身房会员福利
     */
    public Benefit createGymMembership() {
        BenefitCost cost = new BenefitCost(
            new BigDecimal("200"),  // 企业承担
            BigDecimal.ZERO
        );
        
        return createBenefit(
            "健身房会员",
            "公司附近健身房年卡",
            BenefitType.GYM_MEMBERSHIP,
            cost,
            "所有正式员工"
        );
    }
    
    /**
     * 创建标准福利包（新员工基础福利）
     * 
     * @return 标准福利列表
     */
    public java.util.List<Benefit> createStandardBenefitPackage() {
        java.util.List<Benefit> benefits = new java.util.ArrayList<>();
        
        // 基础福利
        benefits.add(createMealAllowance(new BigDecimal("500")));
        
        return benefits;
    }
    
    /**
     * 创建正式员工福利包（转正后）
     * 
     * @return 正式员工福利列表
     */
    public java.util.List<Benefit> createFormalEmployeeBenefitPackage() {
        java.util.List<Benefit> benefits = new java.util.ArrayList<>();
        
        benefits.add(createSocialInsurance());
        benefits.add(createHousingFund());
        benefits.add(createMealAllowance(new BigDecimal("500")));
        benefits.add(createTransportAllowance(new BigDecimal("300")));
        benefits.add(createGymMembership());
        
        return benefits;
    }
}


