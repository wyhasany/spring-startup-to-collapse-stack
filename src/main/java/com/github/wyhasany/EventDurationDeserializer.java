package com.github.wyhasany;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.wyhasany.model.Event;

import java.io.IOException;
import java.time.Duration;

public class EventDurationDeserializer extends StdDeserializer<Duration> {
    protected EventDurationDeserializer() {
        super(Event.class);
    }

    @Override
    public Duration deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        var token = parser.readValueAsTree();
        if (token instanceof TextNode textNode) {
            // Spring Boot 3 provides the duration as a String: PT0.020121865S
            return Duration.parse(textNode.asText());
        } else if (token instanceof DoubleNode doubleNode) {
            // Spring Boot 2 provides the duration as a Double: 0.020121865
            return Duration.parse("PT" + String.format("%f", doubleNode.asDouble()) + "S");
        }
        throw new IllegalArgumentException("Unable to parse duration, not a known type: " + token);
    }
}