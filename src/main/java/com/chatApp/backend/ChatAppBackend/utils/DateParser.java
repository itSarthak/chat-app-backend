package com.chatApp.backend.ChatAppBackend.utils;

import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class DateParser {
    public Date parseStringToDate(String date) {
        OffsetDateTime odt = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return Date.from(odt.toInstant());
    }
}
