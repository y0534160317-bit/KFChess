package org.kfchess.shared.network;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.kfchess.shared.network.messages.JumpMessage;
import org.kfchess.shared.network.messages.LoginMessage;
import org.kfchess.shared.network.messages.MoveMessage;
import org.kfchess.shared.network.messages.PlayMessage;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MoveMessage.class, name = "MOVE"),
        @JsonSubTypes.Type(value = JumpMessage.class, name = "JUMP"),
        @JsonSubTypes.Type(value = LoginMessage.class, name = "LOGIN"),
        @JsonSubTypes.Type(value = PlayMessage.class, name = "PLAY")
})
public abstract class ClientMessage {
}