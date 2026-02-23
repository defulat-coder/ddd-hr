package com.cy.hr.personnel.interfaces.rest;

/**
 * 文件说明：EmployeeController
 */
import com.cy.hr.personnel.application.ChangeEmployeeStatusCommand;
import com.cy.hr.personnel.application.EmployeeApplicationService;
import com.cy.hr.personnel.application.OnboardEmployeeCommand;
import com.cy.hr.personnel.domain.Employee;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "人事档案")
public class EmployeeController {

    private final EmployeeApplicationService service;

    @PostMapping("/onboard")
    @Operation(summary = "员工入职")
    public EmployeeResponse onboard(@Valid @RequestBody OnboardEmployeeCommand command) {
        return toResponse(service.onboard(command));
    }

    @PostMapping("/{employeeId}/regularize")
    @Operation(summary = "员工转正")
    public EmployeeResponse regularize(@Parameter(description = "员工ID") @PathVariable String employeeId) {
        return toResponse(service.becomeRegular(new ChangeEmployeeStatusCommand(employeeId, null, null)));
    }

    @PostMapping("/{employeeId}/transfer")
    @Operation(summary = "员工调岗")
    public EmployeeResponse transfer(@Parameter(description = "员工ID") @PathVariable String employeeId,
                                     @Valid @RequestBody TransferEmployeeRequest request) {
        return toResponse(service.transfer(new ChangeEmployeeStatusCommand(employeeId, request.targetDepartmentId(), request.targetPosition())));
    }

    @PostMapping("/{employeeId}/resign")
    @Operation(summary = "员工离职")
    public EmployeeResponse resign(@Parameter(description = "员工ID") @PathVariable String employeeId) {
        return toResponse(service.resign(new ChangeEmployeeStatusCommand(employeeId, null, null)));
    }

    @GetMapping
    @Operation(summary = "查询员工列表")
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

    @Schema(name = "TransferEmployeeRequest", description = "员工调岗请求")
    public record TransferEmployeeRequest(
            @Schema(description = "目标部门ID") @NotBlank String targetDepartmentId,
            @Schema(description = "目标岗位") @NotBlank String targetPosition
    ) {
    }

    @Schema(name = "EmployeeResponse", description = "员工响应")
    public record EmployeeResponse(
            @Schema(description = "员工ID") String id,
            @Schema(description = "员工工号") String employeeNo,
            @Schema(description = "姓名") String name,
            @Schema(description = "性别") String gender,
            @Schema(description = "出生日期") String birthDate,
            @Schema(description = "身份证号") String idCardNo,
            @Schema(description = "联系电话") String phone,
            @Schema(description = "部门ID") String departmentId,
            @Schema(description = "岗位") String position,
            @Schema(description = "入职日期") String onboardingDate,
            @Schema(description = "员工状态") String status
    ) {
    }
}
