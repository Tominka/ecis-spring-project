package cz.ecis.db.ent;

import cz.ecis.core.ent.CreatorAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "log", name = "log_entry")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class LogEntryEnt extends CreatorAwareEntity<Long> {

    private static final long serialVersionUID = -1L;

    @Column(name = "username", nullable = true, length = 255)
    private String username;

    @Column(name = "id_user", nullable = true)
    private Long userId;

    @Column(name = "session", nullable = true, length = 64)
    private String sessionId;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "user_agent", nullable = true, length = 512)
    private String userAgent;

    @Column(name = "point", nullable = false)
    private String point;

    @Column(name = "authenticated", nullable = false)
    private boolean authenticated;

    @Column(name = "authorized", nullable = false)
    private boolean authorized;

    @Column(name = "password_expired", nullable = false)
    private boolean passwordExpired;

    @Column(name = "is_refresh", nullable = false)
    private boolean isRefresh;

}
