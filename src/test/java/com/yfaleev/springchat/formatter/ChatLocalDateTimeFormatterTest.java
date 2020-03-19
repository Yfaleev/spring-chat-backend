package com.yfaleev.springchat.formatter;

import com.yfaleev.springchat.format.datetime.api.LocalDateTimeFormatter;
import com.yfaleev.springchat.format.datetime.impl.ChatLocalDateTimeFormatterBean;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatLocalDateTimeFormatterTest {

    private LocalDateTimeFormatter localDateTimeFormatter = new ChatLocalDateTimeFormatterBean();

    @Test
    public void whenFormatLocalDateTime_ThenReturnCorrectlyFormattedString() {
        LocalDateTime localDateTime = LocalDateTime.of(2020, 1, 15, 11, 12, 13);
        String expectedString = "2020-01-15 11:12:13";

        assertEquals(expectedString, localDateTimeFormatter.format(localDateTime));
    }

}
