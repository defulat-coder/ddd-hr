package com.company.hr.culture.interfaces.rest;

import com.company.hr.culture.application.CultureActivityApplicationService;
import com.company.hr.culture.application.dto.CreateActivityCommand;
import com.company.hr.culture.application.dto.CultureActivityDTO;
import com.company.hr.culture.application.dto.RegisterParticipantCommand;
import com.company.hr.culture.domain.model.ActivityStatus;
import com.company.hr.culture.domain.model.ActivityType;
import com.company.hr.infrastructure.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
@Tag(name = "企业文化", description = "文化活动管理接口")
public class CultureActivityController {

    private final CultureActivityApplicationService activityService;

    @PostMapping
    @Operation(summary = "创建活动")
    public ApiResponse<CultureActivityDTO> createActivity(@RequestBody CreateActivityCommand command) {
        return ApiResponse.success(activityService.createActivity(command));
    }

    @PostMapping("/{activityId}/open-registration")
    @Operation(summary = "开放报名")
    public ApiResponse<Void> openRegistration(@Parameter(description = "活动ID", required = true) @PathVariable String activityId) {
        activityService.openRegistration(activityId);
        return ApiResponse.success(null, "开放报名成功");
    }

    @PostMapping("/register")
    @Operation(summary = "员工报名")
    public ApiResponse<Void> register(@RequestBody RegisterParticipantCommand command) {
        activityService.registerParticipant(command.getActivityId(), command.getEmployeeId());
        return ApiResponse.success(null, "报名成功");
    }

    @PostMapping("/{activityId}/close-registration")
    @Operation(summary = "关闭报名")
    public ApiResponse<Void> closeRegistration(@Parameter(description = "活动ID", required = true) @PathVariable String activityId) {
        activityService.closeRegistration(activityId);
        return ApiResponse.success(null, "关闭报名成功");
    }

    @PostMapping("/{activityId}/start")
    @Operation(summary = "开始活动")
    public ApiResponse<Void> start(@Parameter(description = "活动ID", required = true) @PathVariable String activityId) {
        activityService.startActivity(activityId);
        return ApiResponse.success(null, "活动开始成功");
    }

    @PostMapping("/{activityId}/complete")
    @Operation(summary = "完成活动")
    public ApiResponse<Void> complete(@Parameter(description = "活动ID", required = true) @PathVariable String activityId) {
        activityService.completeActivity(activityId);
        return ApiResponse.success(null, "活动完成成功");
    }

    @PostMapping("/{activityId}/cancel")
    @Operation(summary = "取消活动")
    public ApiResponse<Void> cancel(
        @Parameter(description = "活动ID", required = true) @PathVariable String activityId,
        @Parameter(description = "取消原因", required = true) @RequestParam String reason) {
        activityService.cancelActivity(activityId, reason);
        return ApiResponse.success(null, "活动取消成功");
    }

    @GetMapping("/{activityId}")
    @Operation(summary = "查询活动详情")
    public ApiResponse<CultureActivityDTO> getById(@Parameter(description = "活动ID", required = true) @PathVariable String activityId) {
        return ApiResponse.success(activityService.getById(activityId));
    }

    @GetMapping
    @Operation(summary = "查询全部活动")
    public ApiResponse<List<CultureActivityDTO>> getAll() {
        return ApiResponse.success(activityService.getAll());
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "按类型查询活动")
    public ApiResponse<List<CultureActivityDTO>> getByType(@Parameter(description = "活动类型", required = true) @PathVariable ActivityType type) {
        return ApiResponse.success(activityService.getByType(type));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询活动")
    public ApiResponse<List<CultureActivityDTO>> getByStatus(@Parameter(description = "活动状态", required = true) @PathVariable ActivityStatus status) {
        return ApiResponse.success(activityService.getByStatus(status));
    }

    @GetMapping("/organizer/{organizerId}")
    @Operation(summary = "按组织者查询活动")
    public ApiResponse<List<CultureActivityDTO>> getByOrganizer(
        @Parameter(description = "组织者员工ID", required = true) @PathVariable String organizerId) {
        return ApiResponse.success(activityService.getByOrganizer(organizerId));
    }
}
