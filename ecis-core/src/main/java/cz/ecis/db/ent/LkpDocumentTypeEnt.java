package cz.ecis.db.ent;

import java.util.UUID;

import org.hibernate.envers.Audited;

import cz.ecis.core.ent.lookup.EcisLookupEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "lookup", name = "lkp_document_type")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Audited
public class LkpDocumentTypeEnt extends EcisLookupEntity<UUID> {

    @Column(name = "extension", length = 50, nullable = false)
    private String extension;

}