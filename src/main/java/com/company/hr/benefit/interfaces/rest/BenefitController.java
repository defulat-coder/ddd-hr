package com.company.hr.benefit.interfaces.rest;

import com.company.hr.benefit.application.BenefitApplicationService;
import com.company.hr.benefit.application.dto.BenefitDTO;
import com.company.hr.benefit.application.dto.CreateBenefitCommand;
import com.company.hr.benefit.application.dto.EnrollBenefitCommand;
import com.company.hr.benefit.domain.model.BenefitType;
import com.company.hr.infrastructure.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/benefits")
@RequiredArgsConstructor
@Tag(name = "福利管理", description = "福利定义和员工参加接口")
public class BenefitController {

    private final BenefitApplicationService benefitService;

    @PostMapping
    @Operation(summary = "创建福利")
    public ApiResponse<BenefitDTO> createBenefit(@RequestBody CreateBenefitCommand command) {
        return ApiResponse.success(benefitService.createBenefit(command));
    }

    @PostMapping("/enroll")
    @Operation(summary = "员工参加福利")
    public ApiResponse<Void> enrollBenefit(@RequestBody EnrollBenefitCommand command) {
        benefitService.enrollBenefit(command);
        return ApiResponse.success(null, "参加福利成功");
    }

    @PostMapping("/{benefitId}/activate")
    @Operation(summary = "启用福利")
    public ApiResponse<Void> activateBenefit(@Parameter(description = "福利ID", required = true) @PathVariable String benefitId) {
        benefitService.activateBenefit(benefitId);
        return ApiResponse.success(null, "福利启用成功");
    }

    @PostMapping("/{benefitId}/deactivate")
    @Operation(summary = "停用福利")
    public ApiResponse<Void> deactivateBenefit(@Parameter(description = "福利ID", required = true) @PathVariable String benefitId) {
        benefitService.deactivateBenefit(benefitId);
        return ApiResponse.success(null, "福利停用成功");
    }

    @GetMapping("/{benefitId}")
    @Operation(summary = "查询福利详情")
    public ApiResponse<BenefitDTO> getById(@Parameter(description = "福利ID", required = true) @PathVariable String benefitId) {
        return ApiResponse.success(benefitService.getById(benefitId));
    }

    @GetMapping
    @Operation(summary = "查询全部福利")
    public ApiResponse<List<BenefitDTO>> getAll() {
        return ApiResponse.success(benefitService.getAll());
    }

    @GetMapping("/active")
    @Operation(summary = "查询启用福利")
    public ApiResponse<List<BenefitDTO>> getActive() {
        return ApiResponse.success(benefitService.getActive());
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "按类型查询福利")
    public ApiResponse<List<BenefitDTO>> getByType(@Parameter(description = "福利类型", required = true) @PathVariable BenefitType type) {
        return ApiResponse.success(benefitService.getByType(type));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "按员工查询福利")
    public ApiResponse<List<BenefitDTO>> getByEmployeeId(
        @Parameter(description = "员工ID", required = true) @PathVariable String employeeId) {
        return ApiResponse.success(benefitService.getByEmployeeId(employeeId));
    }
}
