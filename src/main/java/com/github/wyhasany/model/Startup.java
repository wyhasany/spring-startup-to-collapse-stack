package com.github.wyhasany.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Startup(
        StartupDto startupDto,
        List<Event> sortedEvents,
        Map<Integer, String> idToFullName,
        Map<Integer, Event> idToEvent
    ) {
        public Startup(StartupWrapper wrapper) {
            this(
                wrapper.startupDto(),
                wrapper.sortedEvents(),
                idToFullName(wrapper),
                wrapper.idToEvent()
            );
        }

        private static final String DELIMETER = ";";

        private static HashMap<Integer, String> idToFullName(StartupWrapper wrapper) {
            var idToFullName = new HashMap<Integer, String>();
            var idToEventName = wrapper.idToEventName();
            var idToEvent = wrapper.idToEvent();
            for (Event event : wrapper.sortedEvents()) {
                StringBuilder fullName = new StringBuilder(idToEventName.get(event.id()));
                var tmpEvent = event;
                while (tmpEvent != null && tmpEvent.hasParent()) {
                    fullName.insert(0, idToEventName.get(tmpEvent.parentId()) + DELIMETER);
                    tmpEvent = idToEvent.get(tmpEvent.parentId());
                }
                idToFullName.put(event.id(), fullName.toString());
            }
            return idToFullName;
        }

        public String keyOf(Event event) {
            return idToFullName.get(event.id());
        }

        public String parentKey(Event event) {
            return idToFullName.get(event.parentId());
        }
    }