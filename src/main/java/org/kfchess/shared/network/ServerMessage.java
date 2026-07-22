package org.kfchess.shared.network;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.kfchess.shared.network.messages.GameStateMessage;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GameStateMessage.class, name = "GAME_STATE")
})
public abstract class ServerMessage {
}