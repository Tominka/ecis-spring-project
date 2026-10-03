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
@Table(schema = "log", name = "log_login")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class LogLoginEnt extends CreatorAwareEntity<Long> {

    private static final long serialVersionUID = -1L;

    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "logged_in_at", nullable = false)
    private OffsetDateTime loggedInAt;

    @Column(name = "authorized", nullable = false)
    private boolean authorized;

    @Column(name = "password_expired", nullable = false)
    private boolean passwordExpired;

}
