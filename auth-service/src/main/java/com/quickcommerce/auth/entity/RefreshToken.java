package com.quickcommerce.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import com.quickcommerce.shared.entity.BaseEntity;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens", schema = "auth",
        indexes = {
                @Index(name = "idx_refresh_user_active", columnList = "user_id, revoked_at"),
                @Index(name = "idx_refresh_expires", columnList = "expires_at")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 128)
    private String tokenHash;

    @Column(name = "device_id", length = 128)
    private String deviceId;

    @Column(name = "ip", length = 64)
    private String ip;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "replaced_by")
    private UUID replacedBy;

    @Column(name = "reuse_detected")
    private Boolean reuseDetected;

}
