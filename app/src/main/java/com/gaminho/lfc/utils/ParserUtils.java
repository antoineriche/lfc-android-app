package com.gaminho.lfc.utils;

import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Created by Bonnie on 17/04/2022
 */
public final class ParserUtils {

    public static double extractDoubleFromTextView(final TextView textView) {
        return Objects.nonNull(textView) && Objects.nonNull(textView.getText())
                ? extractDoubleFromString(textView.getText().toString()) : 0D;
    }

    public static double extractDoubleFromString(final String text) {
        return StringUtils.isNotBlank(text) ? Double.parseDouble(text) : 0D;
    }

    public static int extractIntegerFromTextView(final TextView textView) {
        return Objects.nonNull(textView) && StringUtils.isNotBlank(textView.getText()) ?
                Integer.parseInt(textView.getText().toString()) : 0;
    }

    public static LocalDate extractDateFromTextView(final TextView textView,
                                                    final DateTimeFormatter formatter) {
        if (Objects.nonNull(textView) && StringUtils.isNotBlank(textView.getText())) {
            final String sDate = textView.getText().toString();
            try {
                return LocalDate.parse(sDate, formatter);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    public static LocalDate extractDateFromTextView(final TextView textView,
                                                    final String format) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return extractDateFromTextView(textView, formatter);
    }

    public static LocalDate extractDateFromTextView(final TextView textView) {
        return extractDateFromTextView(textView, DateParser.DEFAULT_DATE_FORMATTER);
    }

    private ParserUtils() {
    }
}
