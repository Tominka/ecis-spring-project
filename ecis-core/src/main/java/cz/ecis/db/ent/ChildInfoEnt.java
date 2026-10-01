package cz.ecis.db.ent;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import cz.ecis.core.ent.ILovEntity;
import cz.ecis.core.ent.VersionAwareEntity;
import cz.ecis.core.mapper.LookupBaseMapper;
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
@Table(schema = "ecis", name = "child_info")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Audited
public class ChildInfoEnt extends VersionAwareEntity<Long> implements ILovEntity{

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_child_info", referencedColumnName = "id", updatable = false, insertable = false, nullable = false)
    private LkpChildInfoEnt lkpChildInfo;

    @Column(name = "value", nullable = false)
    private String value;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_child", referencedColumnName = "id", updatable = false, insertable = false, nullable = false)
    private ChildEnt child;

    @Override
    public String getLabel() {
        return LookupBaseMapper.getLovMappingLabel(lkpChildInfo);
    }
}