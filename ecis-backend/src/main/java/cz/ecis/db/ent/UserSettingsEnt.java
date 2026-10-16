package cz.ecis.db.ent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import cz.ecis.core.ent.VersionAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(schema = "security", name = "user_settings")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class UserSettingsEnt extends VersionAwareEntity<Long> {

    private static final long serialVersionUID = -1L;

    @Column(name = "default_page", length = 512, insertable = true, updatable = true)
    private String defaultPage;

    @Column(name = "default_page_after_login", insertable = true, updatable = true)
    private boolean defaultPageAfterLogin;

    @Column(name = "default_page_after_refresh", insertable = true, updatable = true)
    private boolean defaultPageAfterRefresh;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_camp", referencedColumnName = "id", updatable = true, nullable = true)
    private CampEnt camp;
}