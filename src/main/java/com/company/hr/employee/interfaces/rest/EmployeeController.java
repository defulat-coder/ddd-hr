package com.company.hr.employee.interfaces.rest;

import com.company.hr.employee.application.EmployeeApplicationService;
import com.company.hr.employee.application.dto.*;
import com.company.hr.infrastructure.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工REST控制器
 */
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    
    private final EmployeeApplicationService employeeService;
    
    /**
     * 创建员工（入职）
     */
    @PostMapping
    public ApiResponse<EmployeeDTO> createEmployee(@RequestBody CreateEmployeeCommand command) {
        EmployeeDTO employee = employeeService.createEmployee(command);
        return ApiResponse.success(employee, "员工创建成功");
    }
    
    /**
     * 员工转正
     */
    @PostMapping("/{employeeId}/confirm")
    public ApiResponse<Void> confirmEmployee(@PathVariable String employeeId) {
        employeeService.confirmEmployee(employeeId);
        return ApiResponse.success(null, "员工转正成功");
    }
    
    /**
     * 员工调动
     */
    @PostMapping("/transfer")
    public ApiResponse<Void> transferEmployee(@RequestBody TransferEmployeeCommand command) {
        employeeService.transferEmployee(command);
        return ApiResponse.success(null, "员工调动成功");
    }
    
    /**
     * 更新联系信息
     */
    @PutMapping("/contact")
    public ApiResponse<Void> updateContact(@RequestBody UpdateContactCommand command) {
        employeeService.updateContactInfo(command);
        return ApiResponse.success(null, "联系信息更新成功");
    }
    
    /**
     * 员工离职/辞退
     */
    @PostMapping("/resign")
    public ApiResponse<Void> resignEmployee(@RequestBody ResignEmployeeCommand command) {
        employeeService.resignEmployee(command);
        return ApiResponse.success(null, "员工离职成功");
    }
    
    /**
     * 员工晋升
     */
    @PostMapping("/promote")
    public ApiResponse<Void> promoteEmployee(@RequestBody PromoteEmployeeCommand command) {
        employeeService.promoteEmployee(command);
        return ApiResponse.success(null, "员工晋升成功");
    }
    
    /**
     * 提前转正
     */
    @PostMapping("/{employeeId}/confirm-early")
    public ApiResponse<Void> confirmEmployeeEarly(
            @PathVariable String employeeId,
            @RequestParam String reason) {
        employeeService.confirmEmploymentEarly(employeeId, reason);
        return ApiResponse.success(null, "提前转正成功");
    }
    
    /**
     * 延长试用期
     */
    @PostMapping("/extend-probation")
    public ApiResponse<Void> extendProbation(@RequestBody ExtendProbationCommand command) {
        employeeService.extendProbation(command);
        return ApiResponse.success(null, "延长试用期成功");
    }
    
    /**
     * 停职
     */
    @PostMapping("/{employeeId}/suspend")
    public ApiResponse<Void> suspendEmployee(
            @PathVariable String employeeId,
            @RequestParam String reason) {
        employeeService.suspendEmployee(employeeId, reason);
        return ApiResponse.success(null, "员工停职成功");
    }
    
    /**
     * 复职
     */
    @PostMapping("/{employeeId}/reinstate")
    public ApiResponse<Void> reinstateEmployee(@PathVariable String employeeId) {
        employeeService.reinstateEmployee(employeeId);
        return ApiResponse.success(null, "员工复职成功");
    }
    
    /**
     * 根据ID查询员工
     */
    @GetMapping("/{employeeId}")
    public ApiResponse<EmployeeDTO> getEmployee(@PathVariable String employeeId) {
        EmployeeDTO employee = employeeService.getEmployeeById(employeeId);
        return ApiResponse.success(employee);
    }
    
    /**
     * 根据工号查询员工
     */
    @GetMapping("/number/{employeeNumber}")
    public ApiResponse<EmployeeDTO> getEmployeeByNumber(@PathVariable String employeeNumber) {
        EmployeeDTO employee = employeeService.getEmployeeByNumber(employeeNumber);
        return ApiResponse.success(employee);
    }
    
    /**
     * 查询部门员工列表
     */
    @GetMapping("/department/{departmentId}")
    public ApiResponse<List<EmployeeDTO>> getEmployeesByDepartment(@PathVariable String departmentId) {
        List<EmployeeDTO> employees = employeeService.getEmployeesByDepartment(departmentId);
        return ApiResponse.success(employees);
    }
    
    /**
     * 查询所有员工
     */
    @GetMapping
    public ApiResponse<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        return ApiResponse.success(employees);
    }
}

