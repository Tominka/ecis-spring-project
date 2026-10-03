package cz.ecis.db.ent;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import cz.ecis.core.ent.CreatorAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "security", name = "login_attempt")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class LoginAttemptEnt extends CreatorAwareEntity<Long> {

    private static final long serialVersionUID = -1L;

    @Column(name = "ip_address", nullable = false, length = 255)
    private String ipAddress;

    @Column(name = "attempts_max", nullable = false)
    private Integer attemptsMax;

    @Column(name = "attempts_current", nullable = false)
    private Integer attemptsCurrent;

    @Column(name = "attempts_total", nullable = false)
    private Integer attemptsTotal;

    @Column(name = "last_attempt", nullable = true)
    private OffsetDateTime lastAttempt;

    @Column(name = "history", nullable = false)
    private boolean history;

    @Column(name = "unblocked_count", nullable = false)
    private Integer unblockedCount;

    @Column(name = "unblocked_count_user", nullable = false)
    private Integer unblockedCountUser;

    public void onAttempt() {
        this.attemptsCurrent++;
        this.attemptsTotal++;
        this.lastAttempt = OffsetDateTime.now(ZoneId.systemDefault());
    }

}
