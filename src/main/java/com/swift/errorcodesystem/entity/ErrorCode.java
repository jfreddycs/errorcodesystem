package com.swift.errorcodesystem.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "error_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code; // Full code: projectCode-categoryCode-moduleCode-sequence (01-05-15-0001)

    @Column(nullable = false, length = 200)
    private String message;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String suggestedAction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ErrorSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private HttpStatus httpStatus;

    @Column(nullable = false)
    private Integer httpStatusCode;

    @Column(nullable = false)
    private Boolean isRetryable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Module module;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void setHttpStatusCode() {
        if (this.httpStatus != null) {
            this.httpStatusCode = this.httpStatus.getCode();
        }
    }

    public enum ErrorSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum HttpStatus {
        // Client Errors
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        NOT_FOUND(404),
        CONFLICT(409),
        UNPROCESSABLE_ENTITY(422),
        TOO_MANY_REQUESTS(429),

        // Server Errors
        INTERNAL_SERVER_ERROR(500),
        BAD_GATEWAY(502),
        SERVICE_UNAVAILABLE(503);

        private final int code;

        HttpStatus(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static HttpStatus fromCode(int code) {
            for (HttpStatus status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid HTTP status code: " + code);
        }
    }


}