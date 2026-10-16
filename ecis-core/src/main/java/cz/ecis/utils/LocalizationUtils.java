package cz.ecis.utils;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocalizationUtils {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final List<Locale> SUPPORTED_LOCALES = List.of(
        Locale.forLanguageTag("cs"),
        Locale.forLanguageTag("en")
    );
    public static final Locale DEFAULT_LOCALE = Locale.of("en");

    private LocalizationUtils() {
        /* This utility class should not be instantiated */
    }

    public static Locale resolveLocale(String acceptLanguage) {
        if (StringUtils.isBlank(acceptLanguage)) {
            return LocalizationUtils.defaultLocaleTag();
        }

        try {
            Locale locale = Locale.lookup(
                Locale.LanguageRange.parse(acceptLanguage),
                SUPPORTED_LOCALES
            );

            return locale != null ? locale: LocalizationUtils.defaultLocaleTag();

        } catch (IllegalArgumentException _) {
            LOGGER.debug("Invalid Accept-Language '{}', using default locale 'en'", acceptLanguage);

            return LocalizationUtils.defaultLocaleTag();
        }
    }

    public static Locale defaultLocaleTag() {
        return DEFAULT_LOCALE;
    }
}
