package cz.ecis.core.audit;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class EcisAuditId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "id")
    private Long id;

    @Column(name = "id_revision")
    private Long revisionId;
}