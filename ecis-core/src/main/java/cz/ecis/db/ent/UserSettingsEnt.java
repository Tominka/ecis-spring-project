package cz.ecis.db.ent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import cz.ecis.core.ent.VersionAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(schema = "security", name = "user_settings")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class UserSettingsEnt extends VersionAwareEntity<Long> {

    private static final long serialVersionUID = -1L;

    @Column(name = "default_page", length = 512)
    private String defaultPage;

    @Column(name = "default_page_after_login")
    private boolean defaultPageAfterLogin;

    @Column(name = "default_page_after_refresh")
    private boolean defaultPageAfterRefresh;

    @Column(name = "id_camp")
    private Integer campId;
}