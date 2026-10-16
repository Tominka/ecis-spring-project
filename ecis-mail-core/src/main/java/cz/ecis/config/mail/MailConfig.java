package cz.ecis.config.mail;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import cz.ecis.core.mail.MailSession;
import cz.ecis.core.settings.EcisEmailSettings;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

@Configuration
public class MailConfig {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MAIL_TEMPLATE_ENGINE = "mailTemplateEngine";
    public static final String ECIS_MAIL_SESSION = "ecisMailSession";

    public static final String ECIS_MAIL_I18N_LOCATION = "mail.i18n.messages";

    @Bean(MAIL_TEMPLATE_ENGINE)
    TemplateEngine mailTemplateEngine() {

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();

        resolver.setPrefix("mail/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(true);

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        engine.setMessageResolver(this.ecisEmailMesageResolver());

        return engine;
    }

    @Bean(ECIS_MAIL_SESSION)
    MailSession ecisMailSession(EcisEmailSettings settings) {

        Properties props = new Properties();

        if (StringUtils.isBlank(settings.host())) {
            LOGGER.warn("SMTP host is not configured. Email send is disabled.");
            return new MailSession(null);
        } else {
            props.put("mail.smtp.host", settings.host());
        }

        if (settings.port() == null) {
            LOGGER.warn("SMTP port is not configured. Email send is disabled.");
            return new MailSession(null);
        } else {
            props.put("mail.smtp.port", settings.port());
        }

        Session session = null;

        if (StringUtils.isNoneBlank(settings.username(),settings.password())) {
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");

            if (!StringUtils.isAnyEmpty(settings.username(), settings.password())) {
                session = Session.getInstance(props, new Authenticator() {
                    @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(settings.username(), settings.password());
                    }
                });
            } else {
                session = Session.getInstance(props);
            }
        } else {
            session = Session.getInstance(props);
        }

        return new MailSession(session);
    }

    private AbstractMessageResolver ecisEmailMesageResolver() {
        return new AbstractMessageResolver() {

            @Override
            public String resolveMessage(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {

                Locale locale = context.getLocale();
                ResourceBundle bundle = ResourceBundle.getBundle(ECIS_MAIL_I18N_LOCATION, locale);

                if (!bundle.containsKey(key)) {
                    return null;
                }

                String message = bundle.getString(key);

                if (messageParameters != null && messageParameters.length > 0) {
                    return new MessageFormat(message, locale).format(messageParameters);
                }

                return message;
            }

            @Override
            public String createAbsentMessageRepresentation(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
                return "[" + key + "]";
            }
        };
    }
}