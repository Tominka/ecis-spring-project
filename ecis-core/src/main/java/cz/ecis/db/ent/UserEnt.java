package cz.ecis.db.ent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

import cz.ecis.core.ent.VersionAwareEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(schema = "security", name = "user")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class UserEnt extends VersionAwareEntity<Long> {

    private static final long serialVersionUID = -1L;

    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "surname", nullable = false, length = 255)
    private String surname;

    public String getFullname() {
        return new StringBuilder()
            .append(this.name)
            .append(" ")
            .append(this.surname)
        .toString();
    }

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "system_admin", nullable = false)
    private boolean systemAdmin;

    @Column(name = "password_changed", nullable = true)
    private OffsetDateTime passwordChanged;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user_settings", referencedColumnName = "id", updatable = false, nullable = false)
    private UserSettingsEnt settings;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "deleted_at", nullable = true)
    private OffsetDateTime deletedAt;

    @Column(name = "2fa_enabled", nullable = false)
    private boolean twoFaEnabled;

    @Column(name = "2fa_secret", nullable = true, length = 255)
    private String twoFaSecret;
}