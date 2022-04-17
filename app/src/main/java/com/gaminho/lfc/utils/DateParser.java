package com.gaminho.lfc.utils;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Bonnie on 14/04/2022
 */
public final class DateParser {

    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String formatLocalDate(@NonNull final LocalDate localDate) {
        return localDate.format(DEFAULT_DATE_FORMATTER);
    }

    public static String formatEpochDay(final long epochDay) {
        return formatLocalDate(LocalDate.ofEpochDay(epochDay));
    }

    private DateParser() {
    }
}
