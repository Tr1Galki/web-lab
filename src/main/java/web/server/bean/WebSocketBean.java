package web.server.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.faces.event.WebsocketEvent;
import jakarta.faces.push.Push;
import jakarta.faces.push.PushContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Named("webSocketBean")
@ViewScoped
public class WebSocketBean implements Serializable {

    public WebSocketBean() {
    }

    @PostConstruct
    public void init() {
        rOptions = new LinkedHashMap<String, String>();
        rOptions.put("1", "1");
        rOptions.put("1.5", "1.5");
        rOptions.put("2", "2");
        rOptions.put("2.5", "2.5");
        rOptions.put("3", "3");
    }

    @Inject
    @Push
    private PushContext userChannel;

    private String message;

    private String x;
    private String y;
    private String[] r;
    private Map<String, String> rOptions;
    private String owner;
    private String creator;

    public void sendMessage() {
        userChannel.send("message");
        System.out.println("temp_dot" + " x= " + x + " y= " + y + " r= " + Arrays.toString(r) + " ropt= " + rOptions + " owner= " + owner + " creator= " + creator);
    }


    public void onOpen(@Observes @WebsocketEvent.Opened WebsocketEvent opened) {
        userChannel.send("fffffffffffffffffff");
    }

    public void onClose(@Observes @WebsocketEvent.Closed WebsocketEvent closed) {

    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String[] getR() {
        return r;
    }

    public void setR(String[] r) {
        this.r = r;
    }

    public Map getrOptions() {
        return rOptions;
    }

    public void setrOptions(Map rOptions) {
        this.rOptions = rOptions;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
