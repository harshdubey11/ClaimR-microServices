package org.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
        name = "approval-service",
        url = "${approval.service.url}"
)
public interface ApprovalClient {

    @PostMapping("/approval/initiate/{expenseId}")
    void initiateApproval(@PathVariable("expenseId") UUID expenseId);
}