package com.github.wyhasany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@JsonIgnoreProperties(ignoreUnknown = true)
record StartupDto(Timeline timeline) {

    public List<Timeline.Event> sortedEvents() {
        return timeline.events.stream().sorted().toList();
    }

    public Map<Integer, String> idToEventName() {
        return timeline.events.stream()
            .collect(toMap(Timeline.Event::id, Timeline.Event::name));
    }

    public Map<Integer, Timeline.Event> idToEvent() {
        return timeline.events.stream()
            .collect(toMap(Timeline.Event::id, identity()));
    }

    public Startup toStartup() {
        return new Startup(new StartupWrapper(this));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static record Timeline(List<Event> events) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        static record Event(Double duration, StartupStep startupStep) implements Comparable<Event> {

            private static final String DELIMETER = "_";

            private static final String TAGS_SEPARATOR = "?";

            private static final String KEY_VALUE_SEPARATOR = "=";

            private static final String TAG_DELIMETER = "&";

            @Override
            public int compareTo(Event otherEvent) {
                return startupStep.id - otherEvent.startupStep.id;
            }

            public int id() {
                return startupStep.id;
            }

            public String name() {
                var baseName = startupStep.id + DELIMETER + startupStep.name;
                if (startupStep.hasTags()) {
                    baseName = baseName
                        + TAGS_SEPARATOR
                        + startupStep.tags.stream()
                            .map(tag -> tag.key + KEY_VALUE_SEPARATOR + tag.value)
                            .collect(joining(TAG_DELIMETER));
                }
                return baseName;
            }

            public int samples() {
                return (int) (duration * 1_000_000);
            }

            public boolean hasParent() {
                return startupStep.parentId != null;
            }

            public Integer parentId() {
                return startupStep.parentId;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            static record StartupStep(String name, int id, Integer parentId, List<Tags> tags) {
                public boolean hasTags() {
                    return tags != null && !tags.isEmpty();
                }

                @JsonIgnoreProperties(ignoreUnknown = true)
                static record Tags(String key, String value) {
                }
            }
        }
    }


    public static record StartupWrapper(
        StartupDto startupDto,
        List<Timeline.Event> sortedEvents,
        Map<Integer, String> idToEventName,
        Map<Integer, Timeline.Event> idToEvent
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

    public static record Startup(
        StartupDto startupDto,
        List<Timeline.Event> sortedEvents,
        Map<Integer, String> idToFullName,
        Map<Integer, Timeline.Event> idToEvent
    ) {
        public Startup(StartupWrapper wrapper) {
            this(
                wrapper.startupDto,
                wrapper.sortedEvents,
                idToFullName(wrapper),
                wrapper.idToEvent
            );
        }

        private static final String DELIMETER = ";";

        private static HashMap<Integer, String> idToFullName(StartupWrapper wrapper) {
            var idToFullName = new HashMap<Integer, String>();
            var idToEventName = wrapper.idToEventName();
            var idToEvent = wrapper.idToEvent();
            for (Timeline.Event event : wrapper.sortedEvents()) {
                var fullName = idToEventName.get(event.id());
                var tmpEvent = event;
                while (tmpEvent != null && tmpEvent.hasParent()) {
                    fullName = idToEventName.get(tmpEvent.parentId()) + DELIMETER + fullName;
                    tmpEvent = idToEvent.get(tmpEvent.parentId());
                }
                idToFullName.put(event.id(), fullName);
            }
            return idToFullName;
        }

        public String keyOf(Timeline.Event event) {
            return idToFullName.get(event.id());
        }

        public String parentKey(Timeline.Event event) {
            return idToFullName.get(event.parentId());
        }
    }
}
