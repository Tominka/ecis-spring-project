package cz.ecis.db.ent;

import java.time.OffsetDateTime;

import cz.ecis.core.ent.CreatorAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "security", name = "refresh_token")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RefreshTokenEnt extends CreatorAwareEntity<Long> {

    private static final long serialVersionUID = -1L;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "user_agent", nullable = true, length = 255)
    private String userAgent;

    @Column(name = "id_user", nullable = true)
    private Long userId;

    @Column(name = "session", nullable = false, length = 64)
    private String sessionId;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "revoked_at", nullable = true)
    private OffsetDateTime revokedAt;

    @Column(name = "revoked_by_logout", nullable = false)
    private boolean revokedByLogout;

}
