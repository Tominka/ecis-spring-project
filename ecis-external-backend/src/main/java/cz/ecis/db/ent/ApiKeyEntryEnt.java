package cz.ecis.db.ent;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;

import cz.ecis.core.ent.EcisPersistable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "log", name = "api_key_entry")
@Getter
@Setter
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
public class ApiKeyEntryEnt extends EcisPersistable<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "id_api_key", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private ApiKeyEnt apiKey;

    @Column(name = "endpoint", length = 512, nullable = false)
    private String endpoint;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "user_agent", nullable = true, length = 512)
    private String userAgent;

    @Column(name = "payload_length", nullable = false)
    private int payloadLength;

    @Column(name = "success", nullable = true)
    private boolean success;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

}
