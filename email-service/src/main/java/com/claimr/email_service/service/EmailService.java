
package com.claimr.email_service.service;

import com.claimr.email_service.event.ApprovalStatusChangedEvent;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendApprovalNotification(ApprovalStatusChangedEvent event)
            throws MessagingException {

        String status = event.getNewStatus();

        String subject = switch (status) {
            case "MANAGER_APPROVED" -> "Your expense has been approved by your manager";
            case "FINANCE_APPROVED" -> "Your expense has been fully approved";
            case "REJECTED" -> "Your expense has been rejected";
            default -> "Your expense status has been updated";
        };

        String statusColor = "REJECTED".equals(status)
                ? "#dc2626" : "#16a34a";

        String remarks = event.getRemarks() == null
                || event.getRemarks().isBlank()
                ? "No remarks provided" : event.getRemarks();

        String timestamp = event.getTimestamp() == null
                ? "Not available" : event.getTimestamp().toString();

        String html = """
                <html>
                <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                    <div style="max-width: 600px; margin: auto; background: white;
                                padding: 30px; border-radius: 8px;">

                        <h2 style="color: #2563eb;">ClaimR</h2>

                        <h3>Expense Status Update</h3>

                        <p>Your expense request has been updated.</p>

                        <div style="background: #f9fafb; padding: 15px;
                                    border-radius: 6px;">
                            <p><strong>Expense ID:</strong> %s</p>
                            <p><strong>Status:</strong>
                                <span style="color: %s; font-weight: bold;">%s</span>
                            </p>
                            <p><strong>Approval Level:</strong> %s</p>
                            <p><strong>Remarks:</strong> %s</p>
                            <p><strong>Updated At:</strong> %s</p>
                        </div>

                        <p style="margin-top: 25px;">
                            Thank you for using ClaimR.
                        </p>

                        <p style="color: #777; font-size: 12px;">
                            This is an automated notification. Please do not reply.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(
                    event.getExpenseId(),
                    statusColor,
                    status,
                    event.getApprovalLevel(),
                    remarks,
                    timestamp
                );

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(event.getEmployeeEmail());
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(message);
    }
}
