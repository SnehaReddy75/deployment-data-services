package com.slack.model;

import lombok.Data;
import java.time.ZonedDateTime;

/**
 * Created by sneha.dontireddy on 8/4/18.
 */
@Data
public class Event {
    Long id;
    ZonedDateTime date;
    String engineer;
    String action;
    String sha;

    public Event( Long id, String engineer, String action, ZonedDateTime date, String sha) {
        this.date = date;
        this.action = action;
        this.engineer = engineer;
        this.id = id;
        this.sha = sha;
    }
}
