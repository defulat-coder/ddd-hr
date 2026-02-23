package com.cy.hr.personnel.interfaces.rest;

/**
 * 文件说明：EmployeeController
 */
import com.cy.hr.personnel.application.ChangeEmployeeStatusCommand;
import com.cy.hr.personnel.application.EmployeeApplicationService;
import com.cy.hr.personnel.application.OnboardEmployeeCommand;
import com.cy.hr.personnel.domain.Employee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeApplicationService service;

    @PostMapping("/onboard")
    public EmployeeResponse onboard(@Valid @RequestBody OnboardEmployeeCommand command) {
        return toResponse(service.onboard(command));
    }

    @PostMapping("/{employeeId}/regularize")
    public EmployeeResponse regularize(@PathVariable String employeeId) {
        return toResponse(service.becomeRegular(new ChangeEmployeeStatusCommand(employeeId, null, null)));
    }

    @PostMapping("/{employeeId}/transfer")
    public EmployeeResponse transfer(@PathVariable String employeeId,
                                     @Valid @RequestBody TransferEmployeeRequest request) {
        return toResponse(service.transfer(new ChangeEmployeeStatusCommand(employeeId, request.targetDepartmentId(), request.targetPosition())));
    }

    @PostMapping("/{employeeId}/resign")
    public EmployeeResponse resign(@PathVariable String employeeId) {
        return toResponse(service.resign(new ChangeEmployeeStatusCommand(employeeId, null, null)));
    }

    @GetMapping
    public List<EmployeeResponse> list() {
        return service.list().stream().map(this::toResponse).toList();
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeNo(),
                employee.getName(),
                employee.getGender(),
                employee.getBirthDate().toString(),
                employee.getIdCardNo(),
                employee.getPhone(),
                employee.getDepartmentId(),
                employee.getPosition(),
                employee.getOnboardingDate().toString(),
                employee.getStatus().name()
        );
    }

    public record TransferEmployeeRequest(
            @NotBlank String targetDepartmentId,
            @NotBlank String targetPosition
    ) {
    }

    public record EmployeeResponse(
            String id,
            String employeeNo,
            String name,
            String gender,
            String birthDate,
            String idCardNo,
            String phone,
            String departmentId,
            String position,
            String onboardingDate,
            String status
    ) {
    }
}
