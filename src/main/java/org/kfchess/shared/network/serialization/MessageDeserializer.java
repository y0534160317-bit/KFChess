package org.kfchess.shared.network.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kfchess.shared.network.ClientMessage;
import org.kfchess.shared.network.ServerMessage;

public class MessageDeserializer {

    private final ObjectMapper mapper = new ObjectMapper();

    public ClientMessage deserializeClient(String json) {

        try {
            return mapper.readValue(json, ClientMessage.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ServerMessage deserializeServer(String json) {

        try {
            return mapper.readValue(json, ServerMessage.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}