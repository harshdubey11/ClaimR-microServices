
package com.claimr.email_service.consumer;

import com.claimr.email_service.event.ApprovalStatusChangedEvent;
import com.claimr.email_service.service.EmailService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ApprovalEventConsumer {

    private final EmailService emailService;

    public ApprovalEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "approval-status-changed", groupId = "claimr-email-group")
    public void consume(ApprovalStatusChangedEvent event) throws Exception {

        System.out.println(
                "Processing email notification for expense: "
                        + event.getExpenseId());

        emailService.sendApprovalNotification(event);

        System.out.println(
                "Email notification sent successfully to: "
                        + event.getEmployeeEmail());
    }
}

