package com.company.hr.performance.application;

import com.company.hr.employee.domain.model.EmployeeId;
import com.company.hr.performance.application.dto.AddObjectiveCommand;
import com.company.hr.performance.application.dto.CreateGoalCommand;
import com.company.hr.performance.application.dto.GoalDTO;
import com.company.hr.performance.domain.factory.GoalFactory;
import com.company.hr.performance.domain.model.Goal;
import com.company.hr.performance.domain.model.GoalId;
import com.company.hr.performance.domain.model.GoalPeriod;
import com.company.hr.performance.domain.model.GoalStatus;
import com.company.hr.performance.domain.model.Objective;
import com.company.hr.performance.domain.repository.GoalRepository;
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
public class GoalApplicationService {

    private final GoalRepository goalRepository;
    private final GoalFactory goalFactory;
    private final DomainEventPublisher eventPublisher;

    public GoalDTO createGoal(CreateGoalCommand command) {
        GoalPeriod period = new GoalPeriod(command.getStartDate(), command.getEndDate(), command.getRegistrationDeadline());
        period.validate();

        Goal goal = new Goal(
            GoalId.generate(),
            EmployeeId.of(command.getEmployeeId()),
            command.getTitle(),
            command.getDescription(),
            period
        );

        goalRepository.save(goal);
        publishEvents(goal);
        return GoalDTO.fromDomain(goal);
    }

    public void addObjective(AddObjectiveCommand command) {
        Goal goal = getGoal(command.getGoalId());
        Objective objective = goalFactory.createObjective(
            command.getDescription(),
            command.getKeyResult(),
            command.getWeight(),
            command.getTargetValue()
        );
        goal.addObjective(objective);
        goalRepository.save(goal);
        publishEvents(goal);
    }

    public void activateGoal(String goalId) {
        Goal goal = getGoal(goalId);
        goal.activate();
        goalRepository.save(goal);
        publishEvents(goal);
    }

    public void completeGoal(String goalId) {
        Goal goal = getGoal(goalId);
        goal.complete();
        goalRepository.save(goal);
        publishEvents(goal);
    }

    public void cancelGoal(String goalId) {
        Goal goal = getGoal(goalId);
        goal.cancel();
        goalRepository.save(goal);
        publishEvents(goal);
    }

    public GoalDTO getById(String goalId) {
        return GoalDTO.fromDomain(getGoal(goalId));
    }

    public List<GoalDTO> getByEmployeeId(String employeeId) {
        return goalRepository.findByEmployeeId(EmployeeId.of(employeeId)).stream()
            .map(GoalDTO::fromDomain)
            .collect(Collectors.toList());
    }

    public List<GoalDTO> getByEmployeeIdAndStatus(String employeeId, GoalStatus status) {
        return goalRepository.findByEmployeeIdAndStatus(EmployeeId.of(employeeId), status).stream()
            .map(GoalDTO::fromDomain)
            .collect(Collectors.toList());
    }

    private Goal getGoal(String goalId) {
        return goalRepository.findById(GoalId.of(goalId))
            .orElseThrow(() -> new BusinessException("GOAL404", "目标不存在"));
    }

    private void publishEvents(Goal goal) {
        eventPublisher.publishAll(goal.getDomainEvents());
        goal.clearDomainEvents();
    }
}
