package com.yfaleev.springchat.format.datetime.impl;

import com.yfaleev.springchat.format.datetime.DateTimeFormat;
import com.yfaleev.springchat.format.datetime.api.LocalDateTimeFormatter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ChatLocalDateTimeFormatterBean implements LocalDateTimeFormatter {

    @Override
    public String format(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.DATE_WITH_TIME));
    }
}
