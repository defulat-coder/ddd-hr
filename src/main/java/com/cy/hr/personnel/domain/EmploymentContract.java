package com.cy.hr.personnel.domain;

/**
 * 文件说明：EmploymentContract
 */
import com.cy.hr.shared.domain.DomainException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmploymentContract {

    /** 合同类型 */
    private final String type;
    /** 合同开始日期 */
    private final LocalDate startDate;
    /** 合同结束日期 */
    private final LocalDate endDate;
    /** 试用期（月） */
    private final int probationMonths;
    /** 签订日期 */
    private final LocalDate signedDate;

    /**
     * 创建劳动合同值对象。
     */
    public static EmploymentContract create(String type, LocalDate startDate, LocalDate endDate, int probationMonths, LocalDate signedDate) {
        // 业务规则：合同核心信息必填
        if (type == null || type.isBlank()) {
            throw new DomainException("合同类型不能为空");
        }
        if (Objects.isNull(startDate) || Objects.isNull(endDate) || Objects.isNull(signedDate)) {
            throw new DomainException("合同日期不能为空");
        }
        // 业务规则：合同结束时间不能早于开始时间
        if (endDate.isBefore(startDate)) {
            throw new DomainException("合同结束日期不能早于开始日期");
        }
        if (probationMonths < 0) {
            throw new DomainException("试用期不能小于0");
        }
        return new EmploymentContract(type, startDate, endDate, probationMonths, signedDate);
    }

}
