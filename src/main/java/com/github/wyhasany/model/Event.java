package com.github.wyhasany.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.wyhasany.EventDurationDeserializer;

import java.time.Duration;

import static java.util.stream.Collectors.joining;

public record Event(
        @JsonDeserialize(using = EventDurationDeserializer.class)
        Duration duration,
        StartupStep startupStep
) implements Comparable<Event> {

    private static final String DELIMETER = "_";

    private static final String TAGS_SEPARATOR = "?";

    private static final String KEY_VALUE_SEPARATOR = "=";

    private static final String TAG_DELIMETER = "&";

    @Override
    public int compareTo(Event otherEvent) {
        return startupStep.id() - otherEvent.startupStep.id();
    }

    public int id() {
        return startupStep.id();
    }

    public String name() {
        var baseName = startupStep.id() + DELIMETER + startupStep.name();
        if (startupStep.hasTags()) {
            baseName = baseName
                    + TAGS_SEPARATOR
                    + startupStep.tags().stream()
                    .map(tag -> tag.key() + KEY_VALUE_SEPARATOR + tag.value())
                    .collect(joining(TAG_DELIMETER));
        }
        return baseName;
    }

    public int samples() {
        return (int) (duration.toNanos() / 1_000);
    }

    public boolean hasParent() {
        return startupStep.parentId() != null;
    }

    public Integer parentId() {
        return startupStep.parentId();
    }
}