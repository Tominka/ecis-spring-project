package cz.ecis.db.ent;

import java.time.OffsetDateTime;
import java.util.UUID;

import cz.ecis.core.ent.EcisPersistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "security", name = "password_reset")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class PasswordResetEnt extends EcisPersistable<UUID> {

    private static final long serialVersionUID = -1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", referencedColumnName = "id", updatable = false, nullable = false)
    private UserEnt user;

    @Column(name = "ip_address", nullable = true, insertable = true, updatable = false, length = 45)
    private String ipAddress;

    @Column(name = "user_agent", nullable = true, insertable = true, updatable = false, length = 510)
    private String userAgent;

    @Column(name = "token_hash", nullable = false, insertable = true, updatable = true, length = 255)
    private String tokenHash;

    @Column(name = "generated_at", nullable = false, insertable = true, updatable = false)
    private OffsetDateTime generatedAt;

    @Column(name = "last_requested_at", nullable = false, insertable = true, updatable = true)
    private OffsetDateTime lastRequestedAt;

    @Column(name = "valid_to", nullable = false, insertable = true, updatable = true)
    private OffsetDateTime validTo;

    @Column(name = "revoked_at", nullable = true, insertable = false, updatable = true)
    private OffsetDateTime revokedAt;

}
