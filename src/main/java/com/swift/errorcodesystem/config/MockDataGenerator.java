package com.swift.errorcodesystem.config;


import com.swift.errorcodesystem.entity.Category;
import com.swift.errorcodesystem.entity.ErrorCode;
import com.swift.errorcodesystem.entity.Project;
import com.swift.errorcodesystem.service.ErrorCodeService;
import com.swift.errorcodesystem.entity.Module;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MockDataGenerator implements CommandLineRunner {

    private final ErrorCodeService errorCodeService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Generating mock data...");

        // Create Projects
        Project ecommerceProject = createProject("01", "E-Commerce Platform", "Online shopping system", "Platform Team", Project.ProjectStatus.ACTIVE);
        Project bankingProject = createProject("02", "Banking System", "Financial services platform", "Finance Team", Project.ProjectStatus.ACTIVE);
        Project logisticsProject = createProject("03", "Logistics System", "Delivery and tracking system", "Logistics Team", Project.ProjectStatus.ACTIVE);

        // Create Categories for E-Commerce Project
        Category userCategory = createCategory(ecommerceProject, "01", "User Management", "User authentication and profile management");
        Category orderCategory = createCategory(ecommerceProject, "02", "Order Management", "Order processing and management");
        Category paymentCategory = createCategory(ecommerceProject, "03", "Payment Processing", "Payment transactions and processing");
        Category inventoryCategory = createCategory(ecommerceProject, "04", "Inventory Management", "Stock and inventory tracking");

        // Create Categories for Banking Project
        Category accountCategory = createCategory(bankingProject, "01", "Account Management", "Bank account operations");
        Category transactionCategory = createCategory(bankingProject, "02", "Transaction Processing", "Financial transactions");
        Category securityCategory = createCategory(bankingProject, "03", "Security", "Security and fraud detection");

        // Create Modules for E-Commerce Categories
        Module authModule = createModule(userCategory, "01", "Authentication", "User login and authentication", "Handles user authentication and session management");
        Module profileModule = createModule(userCategory, "02", "User Profile", "User profile management", "Manages user profiles and preferences");

        Module orderProcessingModule = createModule(orderCategory, "01", "Order Processing", "Order creation and processing", "Handles order creation, validation, and processing");
        Module orderTrackingModule = createModule(orderCategory, "02", "Order Tracking", "Order status tracking", "Tracks order status and updates");

        Module paymentGatewayModule = createModule(paymentCategory, "01", "Payment Gateway", "Payment processor integration", "Integrates with external payment gateways");
        Module refundModule = createModule(paymentCategory, "02", "Refund Processing", "Refund handling", "Manages refund requests and processing");

        // Create Error Codes for Modules
        createErrorCodesForAuthModule(authModule);
        createErrorCodesForOrderProcessingModule(orderProcessingModule);
        createErrorCodesForPaymentGatewayModule(paymentGatewayModule);

        log.info("Mock data generation completed successfully");
    }

    private Project createProject(String code, String name, String description, String owner, Project.ProjectStatus status) {
        Project project = Project.builder()
                .code(code)
                .name(name)
                .description(description)
                .owner(owner)
                .status(status)
                .build();
        return errorCodeService.createProject(project);
    }

    private Category createCategory(Project project, String code, String name, String description) {
        Category category = Category.builder()
                .code(code)
                .name(name)
                .description(description)
                .build();
        return errorCodeService.createCategory(project.getId(), category);
    }

    private Module createModule(Category category, String code, String name, String description, String purpose) {
        Module module = Module.builder()
                .code(code)
                .name(name)
                .description(description)
                .purpose(purpose)
                .build();
        return errorCodeService.createModule(category.getId(), module);
    }

    private void createErrorCodesForAuthModule(Module module) {
        List<ErrorCode> errorCodes = Arrays.asList(
                createErrorCode(module, "Invalid credentials provided", "Authentication failed due to invalid username or password",
                        "Verify credentials and try again", ErrorCode.ErrorSeverity.MEDIUM, ErrorCode.HttpStatus.UNAUTHORIZED, false),

                createErrorCode(module, "User account is locked", "User account has been locked due to multiple failed login attempts",
                        "Contact administrator to unlock account or use password reset", ErrorCode.ErrorSeverity.HIGH, ErrorCode.HttpStatus.FORBIDDEN, false),

                createErrorCode(module, "Session has expired", "User session has expired due to inactivity",
                        "Re-authenticate to create new session", ErrorCode.ErrorSeverity.LOW, ErrorCode.HttpStatus.UNAUTHORIZED, true),

                createErrorCode(module, "Password does not meet requirements", "Password validation failed",
                        "Use a stronger password that meets security requirements", ErrorCode.ErrorSeverity.MEDIUM, ErrorCode.HttpStatus.BAD_REQUEST, false)
        );

        errorCodes.forEach(errorCode -> errorCodeService.createErrorCode(module.getId(), errorCode));
    }

    private void createErrorCodesForOrderProcessingModule(Module module) {
        List<ErrorCode> errorCodes = Arrays.asList(
                createErrorCode(module, "Insufficient inventory for order", "One or more items in the order are out of stock",
                        "Check inventory levels and update order", ErrorCode.ErrorSeverity.HIGH, ErrorCode.HttpStatus.CONFLICT, false),

                createErrorCode(module, "Invalid order total calculation", "Order total calculation mismatch detected",
                        "Recalculate order total and verify pricing", ErrorCode.ErrorSeverity.MEDIUM, ErrorCode.HttpStatus.BAD_REQUEST, false),

                createErrorCode(module, "Order validation failed", "Order data validation failed",
                        "Check order data and resubmit", ErrorCode.ErrorSeverity.MEDIUM, ErrorCode.HttpStatus.BAD_REQUEST, false),

                createErrorCode(module, "Order processing timeout", "Order processing took too long to complete",
                        "Retry the order processing operation", ErrorCode.ErrorSeverity.MEDIUM, ErrorCode.HttpStatus.SERVICE_UNAVAILABLE, true)
        );

        errorCodes.forEach(errorCode -> errorCodeService.createErrorCode(module.getId(), errorCode));
    }

    private void createErrorCodesForPaymentGatewayModule(Module module) {
        List<ErrorCode> errorCodes = Arrays.asList(
                createErrorCode(module, "Payment gateway timeout", "Payment gateway did not respond in time",
                        "Retry the payment operation", ErrorCode.ErrorSeverity.MEDIUM, ErrorCode.HttpStatus.SERVICE_UNAVAILABLE, true),

                createErrorCode(module, "Payment declined by bank", "Payment was declined by the bank or card issuer",
                        "Contact customer to use alternative payment method", ErrorCode.ErrorSeverity.HIGH, ErrorCode.HttpStatus.BAD_REQUEST, false),

                createErrorCode(module, "Invalid payment details", "Payment details validation failed",
                        "Verify payment details and resubmit", ErrorCode.ErrorSeverity.MEDIUM, ErrorCode.HttpStatus.BAD_REQUEST, false),

                createErrorCode(module, "Payment gateway unreachable", "Cannot connect to payment gateway",
                        "Check network connectivity and gateway status", ErrorCode.ErrorSeverity.CRITICAL, ErrorCode.HttpStatus.BAD_GATEWAY, true)
        );

        errorCodes.forEach(errorCode -> errorCodeService.createErrorCode(module.getId(), errorCode));
    }

    private ErrorCode createErrorCode(Module module, String message, String description, String suggestedAction,
                                      ErrorCode.ErrorSeverity severity, ErrorCode.HttpStatus httpStatus, Boolean isRetryable) {
        return ErrorCode.builder()
                .message(message)
                .description(description)
                .suggestedAction(suggestedAction)
                .severity(severity)
                .httpStatus(httpStatus)
                .isRetryable(isRetryable)
                .build();
    }
}