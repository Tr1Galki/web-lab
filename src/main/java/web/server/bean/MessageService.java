package web.server.bean;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MessageService implements Serializable {
    private List<String> messages;

    @PostConstruct
    private void init() {
        messages = new ArrayList<>();
    }

    public MessageService(){}

    public void add(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
