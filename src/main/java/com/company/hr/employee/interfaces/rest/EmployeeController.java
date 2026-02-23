package com.company.hr.employee.interfaces.rest;

import com.company.hr.employee.application.EmployeeApplicationService;
import com.company.hr.employee.application.dto.*;
import com.company.hr.infrastructure.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工REST控制器
 */
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "员工管理", description = "员工生命周期管理接口")
public class EmployeeController {
    
    private final EmployeeApplicationService employeeService;
    
    /**
     * 创建员工（入职）
     */
    @PostMapping
    @Operation(summary = "创建员工", description = "创建新员工并进入试用期")
    public ApiResponse<EmployeeDTO> createEmployee(@RequestBody CreateEmployeeCommand command) {
        EmployeeDTO employee = employeeService.createEmployee(command);
        return ApiResponse.success(employee, "员工创建成功");
    }
    
    /**
     * 员工转正
     */
    @PostMapping("/{employeeId}/confirm")
    @Operation(summary = "员工转正")
    public ApiResponse<Void> confirmEmployee(
            @Parameter(description = "员工ID", required = true) @PathVariable String employeeId) {
        employeeService.confirmEmployee(employeeId);
        return ApiResponse.success(null, "员工转正成功");
    }
    
    /**
     * 员工调动
     */
    @PostMapping("/transfer")
    @Operation(summary = "员工调动")
    public ApiResponse<Void> transferEmployee(@RequestBody TransferEmployeeCommand command) {
        employeeService.transferEmployee(command);
        return ApiResponse.success(null, "员工调动成功");
    }
    
    /**
     * 更新联系信息
     */
    @PutMapping("/contact")
    @Operation(summary = "更新联系信息")
    public ApiResponse<Void> updateContact(@RequestBody UpdateContactCommand command) {
        employeeService.updateContactInfo(command);
        return ApiResponse.success(null, "联系信息更新成功");
    }
    
    /**
     * 员工离职/辞退
     */
    @PostMapping("/resign")
    @Operation(summary = "员工离职或辞退")
    public ApiResponse<Void> resignEmployee(@RequestBody ResignEmployeeCommand command) {
        employeeService.resignEmployee(command);
        return ApiResponse.success(null, "员工离职成功");
    }
    
    /**
     * 员工晋升
     */
    @PostMapping("/promote")
    @Operation(summary = "员工晋升")
    public ApiResponse<Void> promoteEmployee(@RequestBody PromoteEmployeeCommand command) {
        employeeService.promoteEmployee(command);
        return ApiResponse.success(null, "员工晋升成功");
    }
    
    /**
     * 提前转正
     */
    @PostMapping("/{employeeId}/confirm-early")
    @Operation(summary = "提前转正")
    public ApiResponse<Void> confirmEmployeeEarly(
            @Parameter(description = "员工ID", required = true) @PathVariable String employeeId,
            @Parameter(description = "提前转正原因", required = true) @RequestParam String reason) {
        employeeService.confirmEmploymentEarly(employeeId, reason);
        return ApiResponse.success(null, "提前转正成功");
    }
    
    /**
     * 延长试用期
     */
    @PostMapping("/extend-probation")
    @Operation(summary = "延长试用期")
    public ApiResponse<Void> extendProbation(@RequestBody ExtendProbationCommand command) {
        employeeService.extendProbation(command);
        return ApiResponse.success(null, "延长试用期成功");
    }
    
    /**
     * 停职
     */
    @PostMapping("/{employeeId}/suspend")
    @Operation(summary = "员工停职")
    public ApiResponse<Void> suspendEmployee(
            @Parameter(description = "员工ID", required = true) @PathVariable String employeeId,
            @Parameter(description = "停职原因", required = true) @RequestParam String reason) {
        employeeService.suspendEmployee(employeeId, reason);
        return ApiResponse.success(null, "员工停职成功");
    }
    
    /**
     * 复职
     */
    @PostMapping("/{employeeId}/reinstate")
    @Operation(summary = "员工复职")
    public ApiResponse<Void> reinstateEmployee(
            @Parameter(description = "员工ID", required = true) @PathVariable String employeeId) {
        employeeService.reinstateEmployee(employeeId);
        return ApiResponse.success(null, "员工复职成功");
    }
    
    /**
     * 根据ID查询员工
     */
    @GetMapping("/{employeeId}")
    @Operation(summary = "根据ID查询员工")
    public ApiResponse<EmployeeDTO> getEmployee(
            @Parameter(description = "员工ID", required = true) @PathVariable String employeeId) {
        EmployeeDTO employee = employeeService.getEmployeeById(employeeId);
        return ApiResponse.success(employee);
    }
    
    /**
     * 根据工号查询员工
     */
    @GetMapping("/number/{employeeNumber}")
    @Operation(summary = "根据工号查询员工")
    public ApiResponse<EmployeeDTO> getEmployeeByNumber(
            @Parameter(description = "工号", required = true) @PathVariable String employeeNumber) {
        EmployeeDTO employee = employeeService.getEmployeeByNumber(employeeNumber);
        return ApiResponse.success(employee);
    }
    
    /**
     * 查询部门员工列表
     */
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "查询部门员工列表")
    public ApiResponse<List<EmployeeDTO>> getEmployeesByDepartment(
            @Parameter(description = "部门ID", required = true) @PathVariable String departmentId) {
        List<EmployeeDTO> employees = employeeService.getEmployeesByDepartment(departmentId);
        return ApiResponse.success(employees);
    }
    
    /**
     * 查询所有员工
     */
    @GetMapping
    @Operation(summary = "查询所有员工")
    public ApiResponse<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        return ApiResponse.success(employees);
    }
}
