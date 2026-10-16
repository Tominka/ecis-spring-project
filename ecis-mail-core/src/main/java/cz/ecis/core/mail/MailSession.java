package cz.ecis.core.mail;

import jakarta.mail.Session;
import lombok.Getter;

@Getter
public class MailSession {

    private final Session session;
    private final boolean enabled; 
    
    public MailSession(Session session) {
        this.session = session;
        this.enabled = session != null;
    }
}
