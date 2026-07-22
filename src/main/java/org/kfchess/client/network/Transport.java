package org.kfchess.client.network;



import org.kfchess.shared.network.ClientMessage;
import org.kfchess.shared.network.ServerMessage;

public interface Transport {

    ServerMessage send(ClientMessage message);

}