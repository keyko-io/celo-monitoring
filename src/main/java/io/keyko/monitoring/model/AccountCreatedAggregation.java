package io.keyko.monitoring.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class AccountCreatedAggregation extends BaseAggregation {

    private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private String type;

    public AccountCreatedAggregation(ZonedDateTime dateTime, Long count, String type) {
        super(dateTime, count);
        this.type = type;
    }


    @Override
    protected String formatDateTime(ZonedDateTime datetime) {
        return datetime.format(formatter);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
