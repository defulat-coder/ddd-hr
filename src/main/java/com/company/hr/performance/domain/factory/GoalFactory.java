package com.company.hr.performance.domain.factory;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.performance.domain.model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 目标工厂
 * 负责创建目标（OKR）聚合根
 */
@Component
public class GoalFactory {
    
    /**
     * 创建年度目标
     * 
     * @param employeeId 员工ID
     * @param title 目标标题
     * @param description 目标描述
     * @param year 年度
     * @return 新创建的目标
     */
    public Goal createAnnualGoal(
            EmployeeId employeeId,
            String title,
            String description,
            int year) {
        
        // 生成目标ID
        GoalId goalId = GoalId.generate();
        
        // 创建年度周期（1月1日到12月31日）
        GoalPeriod period = new GoalPeriod(
            LocalDate.of(year, 1, 1),
            LocalDate.of(year, 12, 31),
            LocalDate.of(year, 1, 15) // 1月15日前完成目标设置
        );
        
        period.validate();
        
        // 创建目标聚合根
        Goal goal = new Goal(
            goalId,
            employeeId,
            title,
            description,
            period
        );
        
        return goal;
    }
    
    /**
     * 创建季度目标
     * 
     * @param employeeId 员工ID
     * @param title 目标标题
     * @param description 目标描述
     * @param year 年度
     * @param quarter 季度（1-4）
     * @return 新创建的季度目标
     */
    public Goal createQuarterlyGoal(
            EmployeeId employeeId,
            String title,
            String description,
            int year,
            int quarter) {
        
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("季度必须在1-4之间");
        }
        
        // 生成目标ID
        GoalId goalId = GoalId.generate();
        
        // 计算季度的开始和结束日期
        int startMonth = (quarter - 1) * 3 + 1;
        int endMonth = startMonth + 2;
        
        LocalDate startDate = LocalDate.of(year, startMonth, 1);
        LocalDate endDate = LocalDate.of(year, endMonth, 1).plusMonths(1).minusDays(1);
        LocalDate registrationDeadline = startDate.plusDays(7); // 开始后7天内完成设置
        
        GoalPeriod period = new GoalPeriod(startDate, endDate, registrationDeadline);
        period.validate();
        
        // 创建目标
        Goal goal = new Goal(
            goalId,
            employeeId,
            String.format("Q%d %s", quarter, title),
            description,
            period
        );
        
        return goal;
    }
    
    /**
     * 创建带有标准OKR的目标
     * 
     * @param employeeId 员工ID
     * @param title 目标标题
     * @param description 目标描述
     * @param period 目标周期
     * @param objectives 目标项数据列表
     * @return 带有目标项的目标
     */
    public Goal createGoalWithObjectives(
            EmployeeId employeeId,
            String title,
            String description,
            GoalPeriod period,
            List<ObjectiveCreationData> objectives) {
        
        // 验证权重总和
        BigDecimal totalWeight = objectives.stream()
            .map(ObjectiveCreationData::getWeight)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalWeight.compareTo(new BigDecimal("100")) != 0) {
            throw new IllegalArgumentException("目标项权重总和必须等于100%");
        }
        
        // 创建目标
        Goal goal = new Goal(
            GoalId.generate(),
            employeeId,
            title,
            description,
            period
        );
        
        // 添加目标项
        objectives.forEach(data -> {
            Objective objective = createObjective(
                data.getDescription(),
                data.getKeyResult(),
                data.getWeight(),
                data.getTargetValue()
            );
            goal.addObjective(objective);
        });
        
        return goal;
    }
    
    /**
     * 创建标准的工程师年度目标模板
     * 
     * @param employeeId 员工ID
     * @param year 年度
     * @return 标准工程师目标
     */
    public Goal createEngineerAnnualGoal(EmployeeId employeeId, int year) {
        GoalId goalId = GoalId.generate();
        
        GoalPeriod period = new GoalPeriod(
            LocalDate.of(year, 1, 1),
            LocalDate.of(year, 12, 31),
            LocalDate.of(year, 1, 15)
        );
        
        Goal goal = new Goal(
            goalId,
            employeeId,
            String.format("%d年度工作目标", year),
            "工程师年度关键目标",
            period
        );
        
        // 添加标准目标项
        List<ObjectiveCreationData> objectives = new ArrayList<>();
        
        // O1: 技术能力提升
        objectives.add(new ObjectiveCreationData(
            "技术能力提升",
            "完成3个技术专项学习，通过技术认证考试",
            new BigDecimal("30"),
            new BigDecimal("3")
        ));
        
        // O2: 项目交付
        objectives.add(new ObjectiveCreationData(
            "项目按时交付",
            "负责的项目100%按时交付，质量达标",
            new BigDecimal("40"),
            new BigDecimal("100")
        ));
        
        // O3: 团队协作
        objectives.add(new ObjectiveCreationData(
            "团队协作与分享",
            "完成6次技术分享，帮助2名新人成长",
            new BigDecimal("20"),
            new BigDecimal("6")
        ));
        
        // O4: 创新贡献
        objectives.add(new ObjectiveCreationData(
            "创新与改进",
            "提出并落地3个流程或技术改进方案",
            new BigDecimal("10"),
            new BigDecimal("3")
        ));
        
        objectives.forEach(data -> {
            Objective objective = createObjective(
                data.getDescription(),
                data.getKeyResult(),
                data.getWeight(),
                data.getTargetValue()
            );
            goal.addObjective(objective);
        });
        
        return goal;
    }
    
    /**
     * 创建目标项
     * 
     * @param description 目标项描述
     * @param keyResult 关键结果
     * @param weight 权重
     * @param targetValue 目标值
     * @return 新创建的目标项
     */
    public Objective createObjective(
            String description,
            String keyResult,
            BigDecimal weight,
            BigDecimal targetValue) {
        
        ObjectiveId objectiveId = ObjectiveId.generate();
        
        return new Objective(
            objectiveId,
            description,
            keyResult,
            weight,
            targetValue
        );
    }
    
    /**
     * 目标项创建数据
     */
    public static class ObjectiveCreationData {
        private final String description;
        private final String keyResult;
        private final BigDecimal weight;
        private final BigDecimal targetValue;
        
        public ObjectiveCreationData(String description, String keyResult,
                                    BigDecimal weight, BigDecimal targetValue) {
            this.description = description;
            this.keyResult = keyResult;
            this.weight = weight;
            this.targetValue = targetValue;
        }
        
        public String getDescription() { return description; }
        public String getKeyResult() { return keyResult; }
        public BigDecimal getWeight() { return weight; }
        public BigDecimal getTargetValue() { return targetValue; }
    }
}


