package com.company.hr.benefit.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hr.benefit.domain.model.*;
import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.infrastructure.persistence.BaseEntity;
import com.company.hr.shared.infrastructure.persistence.JsonUtils;
import com.company.hr.shared.infrastructure.persistence.ReflectionUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("benefits")
public class BenefitEntity extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private String id;
    private String name;
    private String description;
    private String type;
    private BigDecimal employerCost;
    private BigDecimal employeeCost;
    private Boolean active;
    private String eligibilityCriteria;
    private String enrollmentsJson;

    public static BenefitEntity fromDomain(Benefit benefit) {
        BenefitEntity entity = new BenefitEntity();
        entity.setId(benefit.getId().getValue());
        entity.setName(benefit.getName());
        entity.setDescription(benefit.getDescription());
        entity.setType(benefit.getType().name());
        entity.setEmployerCost(benefit.getCost().getEmployerCost());
        entity.setEmployeeCost(benefit.getCost().getEmployeeCost());
        entity.setActive(benefit.isActive());
        entity.setEligibilityCriteria(benefit.getEligibilityCriteria());

        List<EnrollmentSnapshot> snapshots = new ArrayList<>();
        for (BenefitEnrollment enrollment : benefit.getEnrollments()) {
            EnrollmentSnapshot snapshot = new EnrollmentSnapshot();
            snapshot.id = enrollment.getId().getValue();
            snapshot.employeeId = enrollment.getEmployeeId().getValue();
            snapshot.enrollmentDate = enrollment.getEnrollmentDate();
            snapshot.effectiveDate = enrollment.getEffectiveDate();
            snapshot.expirationDate = enrollment.getExpirationDate();
            snapshot.status = enrollment.getStatus().name();
            snapshot.notes = enrollment.getNotes();
            snapshots.add(snapshot);
        }
        entity.setEnrollmentsJson(JsonUtils.toJson(snapshots));
        return entity;
    }

    public Benefit toDomain() {
        BenefitCost cost = new BenefitCost(employerCost, employeeCost);
        Benefit benefit = new Benefit(BenefitId.of(id), name, description, BenefitType.valueOf(type), cost, eligibilityCriteria);
        benefit.clearDomainEvents();

        List<BenefitEnrollment> enrollments = new ArrayList<>();
        if (enrollmentsJson != null && !enrollmentsJson.isBlank()) {
            List<EnrollmentSnapshot> snapshots = JsonUtils.fromJson(enrollmentsJson, new TypeReference<List<EnrollmentSnapshot>>() {});
            for (EnrollmentSnapshot snapshot : snapshots) {
                BenefitEnrollment enrollment = new BenefitEnrollment(
                    EnrollmentId.of(snapshot.id),
                    EmployeeId.of(snapshot.employeeId),
                    snapshot.enrollmentDate,
                    snapshot.effectiveDate,
                    snapshot.expirationDate
                );
                ReflectionUtils.setField(enrollment, "status", EnrollmentStatus.valueOf(snapshot.status));
                ReflectionUtils.setField(enrollment, "notes", snapshot.notes);
                enrollments.add(enrollment);
            }
        }

        ReflectionUtils.setField(benefit, "enrollments", enrollments);
        ReflectionUtils.setField(benefit, "active", active != null ? active : Boolean.TRUE);
        return benefit;
    }

    public static class EnrollmentSnapshot {
        public String id;
        public String employeeId;
        public LocalDate enrollmentDate;
        public LocalDate effectiveDate;
        public LocalDate expirationDate;
        public String status;
        public String notes;
    }
}
