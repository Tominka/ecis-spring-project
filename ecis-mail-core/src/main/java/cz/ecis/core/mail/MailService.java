package cz.ecis.core.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import cz.ecis.config.mail.MailConfig;
import cz.ecis.core.settings.EcisEmailSettings;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final EcisEmailSettings ecisEmailSettings;

    @Qualifier(MailConfig.ECIS_MAIL_SESSION)
    private final MailSession mailSession;

    @Qualifier(MailConfig.MAIL_TEMPLATE_ENGINE)
    private final TemplateEngine templateEngine;

    public void sendRender(String to, String subjectKey, String template, Context context) throws MessagingException {
        String html =  this.templateEngine.process(template, context);
        this.send(List.of(to), subjectKey, html, context.getLocale());
    }

    public void sendRender(List<String> to, String subjectKey, String template, Context context) throws MessagingException {
        String html =  this.templateEngine.process(template, context);
        this.send(to, subjectKey, html, context.getLocale());
    }

    public void send(List<String> to, String subjectKey, String html, Locale locale) throws MessagingException {

        if (!mailSession.isEnabled()) {
            LOGGER.warn("SMTP host or port is not configured. Email send is disabled.");
            return;
        }
        MimeMessage message = new MimeMessage(mailSession.getSession());

        message.setFrom(this.getInternetAdress(this.ecisEmailSettings.from()));
        
        if (to.size() > 1) {
            List<InternetAddress> recipients = new ArrayList<>(to.size());

            for (String email : to) {
                recipients.add(
                    this.getInternetAdress(email)
                );
            }

            message.setRecipients(
                Message.RecipientType.BCC,
                recipients.toArray(new InternetAddress[0])
            );
        } else {
            message.setRecipient(Message.RecipientType.TO, this.getInternetAdress(to.get(0)));
        }

        String subject = this.translate(subjectKey, locale);

        message.setSubject(subject, "UTF-8");
        message.setContent(html, "text/html; charset=UTF-8");

        Transport.send(message);

        LOGGER.info("Email was sent to {}.", to.toString());
    }

    private InternetAddress getInternetAdress(String email) throws AddressException {
        return new InternetAddress(email.trim());
    }

    private String translate(String key, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(MailConfig.ECIS_MAIL_I18N_LOCATION, locale);

        if (!bundle.containsKey(key)) {
            return "[" + key + "]";
        }

        return bundle.getString(key);
    }
}