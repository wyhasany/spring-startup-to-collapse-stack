package com.github.wyhasany.model;

import java.util.List;
import java.util.Map;

public record StartupWrapper(
        StartupDto startupDto,
        List<Event> sortedEvents,
        Map<Integer, String> idToEventName,
        Map<Integer, Event> idToEvent
    ) {

        public StartupWrapper(StartupDto startupDto) {
            this(
                startupDto,
                startupDto.sortedEvents(),
                startupDto.idToEventName(),
                startupDto.idToEvent()
            );
        }
    }