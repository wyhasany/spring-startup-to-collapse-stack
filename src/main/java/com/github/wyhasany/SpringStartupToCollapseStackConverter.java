package com.github.wyhasany;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.wyhasany.model.Event;
import com.github.wyhasany.model.StartupDto;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class SpringStartupToCollapseStackConverter {

    ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    public String parse(String json) {
        try {
            var startupDto = objectMapper.readValue(json, StartupDto.class);
            var startup = startupDto.toStartup();
            var stackToSamples = new LinkedHashMap<String, Integer>();
            for (Event event : startup.sortedEvents()) {
                stackToSamples.put(startup.keyOf(event), event.samples());
                if (event.hasParent()) {
                    stackToSamples.merge(
                        startup.parentKey(event),
                        -event.samples(),
                        Integer::sum
                    );
                }
            }
            return stackToSamples
                .entrySet()
                .stream()
                .map(it -> it.getKey() + " " + it.getValue())
                .collect(Collectors.joining("\n"));
        } catch (JsonProcessingException e) {
            System.out.println("Unable to parse json:\n" + json
                + "\n\nCaused by " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
