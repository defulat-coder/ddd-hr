package com.company.hr.organization.interfaces.rest;

import com.company.hr.infrastructure.web.ApiResponse;
import com.company.hr.organization.application.DepartmentApplicationService;
import com.company.hr.organization.application.dto.AddPositionCommand;
import com.company.hr.organization.application.dto.CreateDepartmentCommand;
import com.company.hr.organization.application.dto.DepartmentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
@Tag(name = "组织管理", description = "部门与职位接口")
public class DepartmentController {

    private final DepartmentApplicationService departmentService;

    @PostMapping
    @Operation(summary = "创建部门")
    public ApiResponse<DepartmentDTO> createDepartment(@RequestBody CreateDepartmentCommand command) {
        return ApiResponse.success(departmentService.createDepartment(command));
    }

    @PostMapping("/positions")
    @Operation(summary = "添加职位")
    public ApiResponse<Void> addPosition(@RequestBody AddPositionCommand command) {
        departmentService.addPosition(command);
        return ApiResponse.success(null, "职位添加成功");
    }

    @PostMapping("/{departmentId}/manager")
    @Operation(summary = "更换部门负责人")
    public ApiResponse<Void> changeManager(
        @Parameter(description = "部门ID", required = true) @PathVariable String departmentId,
        @Parameter(description = "负责人ID", required = true) @RequestParam String managerId) {
        departmentService.changeManager(departmentId, managerId);
        return ApiResponse.success(null, "负责人变更成功");
    }

    @PostMapping("/{departmentId}/deactivate")
    @Operation(summary = "停用部门")
    public ApiResponse<Void> deactivate(
        @Parameter(description = "部门ID", required = true) @PathVariable String departmentId) {
        departmentService.deactivateDepartment(departmentId);
        return ApiResponse.success(null, "部门停用成功");
    }

    @PostMapping("/{departmentId}/activate")
    @Operation(summary = "启用部门")
    public ApiResponse<Void> activate(
        @Parameter(description = "部门ID", required = true) @PathVariable String departmentId) {
        departmentService.activateDepartment(departmentId);
        return ApiResponse.success(null, "部门启用成功");
    }

    @GetMapping("/{departmentId}")
    @Operation(summary = "查询部门详情")
    public ApiResponse<DepartmentDTO> getById(
        @Parameter(description = "部门ID", required = true) @PathVariable String departmentId) {
        return ApiResponse.success(departmentService.getById(departmentId));
    }

    @GetMapping
    @Operation(summary = "查询全部部门")
    public ApiResponse<List<DepartmentDTO>> getAll() {
        return ApiResponse.success(departmentService.getAll());
    }

    @GetMapping("/top-level")
    @Operation(summary = "查询顶级部门")
    public ApiResponse<List<DepartmentDTO>> getTopLevel() {
        return ApiResponse.success(departmentService.getTopLevel());
    }

    @GetMapping("/active")
    @Operation(summary = "查询启用部门")
    public ApiResponse<List<DepartmentDTO>> getActive() {
        return ApiResponse.success(departmentService.getActive());
    }
}
