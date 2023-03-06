package com.github.wyhasany.model;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public record StartupDto(String springBootVersion, Timeline timeline) {

    public List<Event> sortedEvents() {
        return timeline.events().stream().sorted().toList();
    }

    public Map<Integer, String> idToEventName() {
        return timeline.events().stream()
                .collect(toMap(Event::id, Event::name));
    }

    public Map<Integer, Event> idToEvent() {
        return timeline.events().stream()
                .collect(toMap(Event::id, identity()));
    }

    public Startup toStartup() {
        return new Startup(new StartupWrapper(this));
    }
}
