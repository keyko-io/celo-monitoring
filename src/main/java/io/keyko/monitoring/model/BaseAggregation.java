package io.keyko.monitoring.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class BaseAggregation {

    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

    protected ZonedDateTime dateTime;
    protected String date;
    protected Long count;

    public BaseAggregation(ZonedDateTime dateTime, Long count) {
        this.dateTime = dateTime;
        this.date = formatDateTime(dateTime);
        this.count = count;
    }


    protected String formatDateTime(ZonedDateTime datetime) {
        return datetime.format(formatter);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
