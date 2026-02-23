package com.company.hr.performance.interfaces.rest;

import com.company.hr.infrastructure.web.ApiResponse;
import com.company.hr.performance.application.GoalApplicationService;
import com.company.hr.performance.application.dto.AddObjectiveCommand;
import com.company.hr.performance.application.dto.CreateGoalCommand;
import com.company.hr.performance.application.dto.GoalDTO;
import com.company.hr.performance.domain.model.GoalStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
@Tag(name = "目标绩效", description = "OKR 目标管理接口")
public class GoalController {

    private final GoalApplicationService goalService;

    @PostMapping
    @Operation(summary = "创建目标")
    public ApiResponse<GoalDTO> createGoal(@RequestBody CreateGoalCommand command) {
        return ApiResponse.success(goalService.createGoal(command));
    }

    @PostMapping("/objectives")
    @Operation(summary = "添加目标项")
    public ApiResponse<Void> addObjective(@RequestBody AddObjectiveCommand command) {
        goalService.addObjective(command);
        return ApiResponse.success(null, "目标项添加成功");
    }

    @PostMapping("/{goalId}/activate")
    @Operation(summary = "激活目标")
    public ApiResponse<Void> activateGoal(@Parameter(description = "目标ID", required = true) @PathVariable String goalId) {
        goalService.activateGoal(goalId);
        return ApiResponse.success(null, "目标激活成功");
    }

    @PostMapping("/{goalId}/complete")
    @Operation(summary = "完成目标")
    public ApiResponse<Void> completeGoal(@Parameter(description = "目标ID", required = true) @PathVariable String goalId) {
        goalService.completeGoal(goalId);
        return ApiResponse.success(null, "目标完成成功");
    }

    @PostMapping("/{goalId}/cancel")
    @Operation(summary = "取消目标")
    public ApiResponse<Void> cancelGoal(@Parameter(description = "目标ID", required = true) @PathVariable String goalId) {
        goalService.cancelGoal(goalId);
        return ApiResponse.success(null, "目标取消成功");
    }

    @GetMapping("/{goalId}")
    @Operation(summary = "查询目标详情")
    public ApiResponse<GoalDTO> getById(@Parameter(description = "目标ID", required = true) @PathVariable String goalId) {
        return ApiResponse.success(goalService.getById(goalId));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "按员工查询目标")
    public ApiResponse<List<GoalDTO>> getByEmployee(@Parameter(description = "员工ID", required = true) @PathVariable String employeeId) {
        return ApiResponse.success(goalService.getByEmployeeId(employeeId));
    }

    @GetMapping("/employee/{employeeId}/status/{status}")
    @Operation(summary = "按员工和状态查询目标")
    public ApiResponse<List<GoalDTO>> getByEmployeeAndStatus(
        @Parameter(description = "员工ID", required = true) @PathVariable String employeeId,
        @Parameter(description = "目标状态", required = true) @PathVariable GoalStatus status) {
        return ApiResponse.success(goalService.getByEmployeeIdAndStatus(employeeId, status));
    }
}
