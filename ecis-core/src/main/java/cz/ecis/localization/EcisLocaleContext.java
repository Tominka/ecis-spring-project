package cz.ecis.localization;

import cz.ecis.utils.LocalizationUtils;
import jakarta.servlet.http.HttpServletRequest;

public class EcisLocaleContext {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    private static final String LANGUAGE_HEADER = "Accept-Language";

    private EcisLocaleContext() {
        /* This utility class should not be instantiated */
    }

    public static void init(HttpServletRequest request) {
        if (CONTEXT.get() == null) {
            CONTEXT.set(getLocale(request));
        }
    }

    public static String getLocale() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

    private static String getLocale(HttpServletRequest request) {
        String locale = request.getHeader(LANGUAGE_HEADER);
        return locale != null ? LocalizationUtils.resolveLocale(locale) : LocalizationUtils.defaultLocaleTag();
    }
}