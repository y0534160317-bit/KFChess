package org.kfchess.shared.network.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageSerializer {

    private final ObjectMapper mapper = new ObjectMapper();

    public String serialize(Object message) {

        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}