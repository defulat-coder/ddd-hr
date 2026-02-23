package com.company.hr.culture.application;

import com.company.hr.culture.application.dto.CreateActivityCommand;
import com.company.hr.culture.application.dto.CultureActivityDTO;
import com.company.hr.culture.domain.factory.CultureActivityFactory;
import com.company.hr.culture.domain.model.*;
import com.company.hr.culture.domain.repository.CultureActivityRepository;
import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.shared.event.DomainEventPublisher;
import com.company.hr.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CultureActivityApplicationService {

    private final CultureActivityRepository activityRepository;
    private final CultureActivityFactory activityFactory;
    private final DomainEventPublisher eventPublisher;

    public CultureActivityDTO createActivity(CreateActivityCommand command) {
        ActivitySchedule schedule = new ActivitySchedule(
            command.getStartTime(),
            command.getEndTime(),
            command.getRegistrationDeadline()
        );

        CultureActivity activity = activityFactory.createActivity(
            command.getTitle(),
            command.getDescription(),
            command.getType(),
            schedule,
            command.getLocation(),
            EmployeeId.of(command.getOrganizerId()),
            command.getMaxParticipants(),
            command.getBudget()
        );

        activityRepository.save(activity);
        publishEvents(activity);
        return CultureActivityDTO.fromDomain(activity);
    }

    public void openRegistration(String activityId) {
        CultureActivity activity = getActivity(activityId);
        activity.openRegistration();
        activityRepository.save(activity);
        publishEvents(activity);
    }

    public void registerParticipant(String activityId, String employeeId) {
        CultureActivity activity = getActivity(activityId);
        activity.registerParticipant(EmployeeId.of(employeeId));
        activityRepository.save(activity);
        publishEvents(activity);
    }

    public void closeRegistration(String activityId) {
        CultureActivity activity = getActivity(activityId);
        activity.closeRegistration();
        activityRepository.save(activity);
        publishEvents(activity);
    }

    public void startActivity(String activityId) {
        CultureActivity activity = getActivity(activityId);
        activity.start();
        activityRepository.save(activity);
        publishEvents(activity);
    }

    public void completeActivity(String activityId) {
        CultureActivity activity = getActivity(activityId);
        activity.complete();
        activityRepository.save(activity);
        publishEvents(activity);
    }

    public void cancelActivity(String activityId, String reason) {
        CultureActivity activity = getActivity(activityId);
        activity.cancel(reason);
        activityRepository.save(activity);
        publishEvents(activity);
    }

    public CultureActivityDTO getById(String activityId) {
        return CultureActivityDTO.fromDomain(getActivity(activityId));
    }

    public List<CultureActivityDTO> getAll() {
        return activityRepository.findAll().stream().map(CultureActivityDTO::fromDomain).collect(Collectors.toList());
    }

    public List<CultureActivityDTO> getByType(ActivityType type) {
        return activityRepository.findByType(type).stream().map(CultureActivityDTO::fromDomain).collect(Collectors.toList());
    }

    public List<CultureActivityDTO> getByStatus(ActivityStatus status) {
        return activityRepository.findByStatus(status).stream().map(CultureActivityDTO::fromDomain).collect(Collectors.toList());
    }

    public List<CultureActivityDTO> getByOrganizer(String organizerId) {
        return activityRepository.findByOrganizerId(EmployeeId.of(organizerId)).stream()
            .map(CultureActivityDTO::fromDomain)
            .collect(Collectors.toList());
    }

    private CultureActivity getActivity(String activityId) {
        return activityRepository.findById(ActivityId.of(activityId))
            .orElseThrow(() -> new BusinessException("ACT404", "活动不存在"));
    }

    private void publishEvents(CultureActivity activity) {
        eventPublisher.publishAll(activity.getDomainEvents());
        activity.clearDomainEvents();
    }
}
