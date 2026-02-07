package services;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LocalizationService {
    private final Locale locale;
    private final ResourceBundle messages;

    public LocalizationService(Locale locale) {
        this.locale = locale;
        this.messages = ResourceBundle.getBundle("resources/messages", locale);
    }

    public String getMessage(String key, Object... params) {
        try {
            String pattern = messages.getString(key);
            return MessageFormat.format(pattern, params);
        }catch(MissingResourceException e) {
            return "[" + key + "]";
        }
    }

    public String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(locale);
        return date.format(formatter);
    }

    public String formatNumber(double number) {
        return String.format(locale, "%.2f", number);
    }

    public static Locale getLocaleForLanguage(String language) {
        return switch (language.toLowerCase()) {
            case "fa", "persian" -> new Locale("fa", "IR");
            case "en" -> Locale.ENGLISH;
            case "de" -> Locale.GERMAN;
            case "fr" -> Locale.FRENCH;
            default -> Locale.getDefault();
        };
    }
}
