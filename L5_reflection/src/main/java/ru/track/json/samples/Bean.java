package ru.track.json.samples;

import ru.track.json.JsonNullable;

/**
 *
 */
@JsonNullable
public class Bean {
    String notNull = "A";
    String nullable = null;

    public Bean() {
    }
}
