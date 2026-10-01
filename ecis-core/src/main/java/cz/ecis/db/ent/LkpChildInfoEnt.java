package cz.ecis.db.ent;

import org.hibernate.envers.Audited;

import cz.ecis.core.ent.lookup.EcisLookupEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "lookup", name = "lkp_child_info")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Audited
public class LkpChildInfoEnt extends EcisLookupEntity<Long> {
    
}
