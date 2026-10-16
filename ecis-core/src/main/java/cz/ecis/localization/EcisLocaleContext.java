package cz.ecis.localization;

import java.util.Locale;

import cz.ecis.utils.LocalizationUtils;
import jakarta.servlet.http.HttpServletRequest;

public class EcisLocaleContext {

    private static final ThreadLocal<Locale> CONTEXT = new ThreadLocal<>();

    private static final String LANGUAGE_HEADER = "Accept-Language";

    private EcisLocaleContext() {
        /* This utility class should not be instantiated */
    }

    public static void init(HttpServletRequest request) {
        if (CONTEXT.get() == null) {
            CONTEXT.set(getLocale(request));
        }
    }

    public static Locale getLocale() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    public static String localeString() {
        return EcisLocaleContext.getLocale().toLanguageTag();
    }

    private static Locale getLocale(HttpServletRequest request) {
        String locale = request.getHeader(LANGUAGE_HEADER);
        return locale != null ? LocalizationUtils.resolveLocale(locale) : LocalizationUtils.defaultLocaleTag();
    }
}