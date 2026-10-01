package cz.ecis.utils;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.ecis.localization.EcisLocaleEnum;

public class LocalizationUtils {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final EcisLocaleEnum DEFAULT_LOCALE = EcisLocaleEnum.EN;

    private LocalizationUtils() {
        /* This utility class should not be instantiated */
    }

    public static String resolveLocale(String acceptLanguage) {
        if (StringUtils.isBlank(acceptLanguage)) {
            return LocalizationUtils.defaultLocaleTag();
        }

        try {
            List<Locale> supportedLocales = List.of(
                Locale.forLanguageTag("cs"),
                Locale.forLanguageTag("en")
            );

            Locale locale = Locale.lookup(
                Locale.LanguageRange.parse(acceptLanguage),
                supportedLocales
            );

            return locale != null ? locale.getLanguage() : LocalizationUtils.defaultLocaleTag();

        } catch (IllegalArgumentException _) {
            LOGGER.debug("Invalid Accept-Language '{}', using default locale 'en'", acceptLanguage);

            return LocalizationUtils.defaultLocaleTag();
        }
    }

    public static String defaultLocaleTag() {
        return DEFAULT_LOCALE.name().toLowerCase(Locale.ROOT);
    }
}
