package de.nurmarvin.axo.utils;

import com.mewna.catnip.util.Utils;
import humanize.Humanize;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static String humanize(OffsetDateTime offsetDateTime) {
        return Humanize.naturalTime(new Date(Instant.now().toEpochMilli()), new Date(offsetDateTime.toInstant().toEpochMilli()));
    }
}
