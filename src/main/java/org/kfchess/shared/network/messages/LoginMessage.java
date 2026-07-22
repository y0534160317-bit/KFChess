package org.kfchess.shared.network.messages;

import org.kfchess.shared.network.ClientMessage;

public class LoginMessage extends ClientMessage {

    private String username;
    private String password;

    public LoginMessage() {
    }

    public LoginMessage(String username,
                        String password) {

        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}