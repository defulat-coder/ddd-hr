package com.company.hr.benefit.application;

import com.company.hr.benefit.application.dto.BenefitDTO;
import com.company.hr.benefit.application.dto.CreateBenefitCommand;
import com.company.hr.benefit.application.dto.EnrollBenefitCommand;
import com.company.hr.benefit.domain.factory.BenefitFactory;
import com.company.hr.benefit.domain.model.*;
import com.company.hr.benefit.domain.repository.BenefitRepository;
import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.event.DomainEventPublisher;
import com.company.hr.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BenefitApplicationService {

    private final BenefitRepository benefitRepository;
    private final BenefitFactory benefitFactory;
    private final DomainEventPublisher eventPublisher;

    public BenefitDTO createBenefit(CreateBenefitCommand command) {
        BenefitCost cost = new BenefitCost(command.getEmployerCost(), command.getEmployeeCost());
        Benefit benefit = benefitFactory.createBenefit(
            command.getName(),
            command.getDescription(),
            command.getType(),
            cost,
            command.getEligibilityCriteria()
        );
        benefitRepository.save(benefit);
        publishEvents(benefit);
        return BenefitDTO.fromDomain(benefit);
    }

    public void enrollBenefit(EnrollBenefitCommand command) {
        Benefit benefit = getBenefit(command.getBenefitId());

        LocalDate enrollmentDate = command.getEnrollmentDate() == null ? LocalDate.now() : command.getEnrollmentDate();
        LocalDate effectiveDate = command.getEffectiveDate() == null ? enrollmentDate : command.getEffectiveDate();

        BenefitEnrollment enrollment = new BenefitEnrollment(
            EnrollmentId.generate(),
            EmployeeId.of(command.getEmployeeId()),
            enrollmentDate,
            effectiveDate,
            command.getExpirationDate()
        );

        enrollment.approve();
        if (!LocalDate.now().isBefore(effectiveDate)) {
            enrollment.activate();
        }

        benefit.addEnrollment(enrollment);
        benefitRepository.save(benefit);
        publishEvents(benefit);
    }

    public void activateBenefit(String benefitId) {
        Benefit benefit = getBenefit(benefitId);
        benefit.activate();
        benefitRepository.save(benefit);
        publishEvents(benefit);
    }

    public void deactivateBenefit(String benefitId) {
        Benefit benefit = getBenefit(benefitId);
        benefit.deactivate();
        benefitRepository.save(benefit);
        publishEvents(benefit);
    }

    public BenefitDTO getById(String benefitId) {
        return BenefitDTO.fromDomain(getBenefit(benefitId));
    }

    public List<BenefitDTO> getAll() {
        return benefitRepository.findAll().stream().map(BenefitDTO::fromDomain).collect(Collectors.toList());
    }

    public List<BenefitDTO> getActive() {
        return benefitRepository.findActiveBenefits().stream().map(BenefitDTO::fromDomain).collect(Collectors.toList());
    }

    public List<BenefitDTO> getByType(BenefitType type) {
        return benefitRepository.findByType(type).stream().map(BenefitDTO::fromDomain).collect(Collectors.toList());
    }

    public List<BenefitDTO> getByEmployeeId(String employeeId) {
        return benefitRepository.findByEmployeeId(EmployeeId.of(employeeId)).stream()
            .map(BenefitDTO::fromDomain)
            .collect(Collectors.toList());
    }

    private Benefit getBenefit(String benefitId) {
        return benefitRepository.findById(BenefitId.of(benefitId))
            .orElseThrow(() -> new BusinessException("BEN404", "福利不存在"));
    }

    private void publishEvents(Benefit benefit) {
        eventPublisher.publishAll(benefit.getDomainEvents());
        benefit.clearDomainEvents();
    }
}
