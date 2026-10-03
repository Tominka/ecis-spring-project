package cz.ecis.db.ent;

import java.util.List;

import cz.ecis.core.ent.VersionAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "security", name = "api_key")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ApiKeyEnt extends VersionAwareEntity<Long> {

    private static final long serialVersionUID = -1L;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "key_hash", nullable = false, length = 255)
    private String keyHash;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "apiKey", fetch = FetchType.LAZY)
    private List<ApiKeyEntryEnt> apiKeyEntries;

}