package web.server.bean;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import jakarta.faces.push.Push;
import jakarta.faces.push.PushContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import web.server.Dot;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeoutException;

@Named("webSocketBean")
@ViewScoped
public class WebSocketBean implements Serializable {

    public WebSocketBean() {
    }

    @PostConstruct
    public void init() {
        rOptions = new LinkedHashMap<>();
        rOptions.put("1", "1");
        rOptions.put("1.5", "1.5");
        rOptions.put("2", "2");
        rOptions.put("2.5", "2.5");
        rOptions.put("3", "3");
    }

    @Inject
    @Push(channel="incoming")
    private PushContext incoming;

    private String x;
    private String canvasX;
    private String y;
    private String canvasY;
    private String[] r;
    private String canvasR;
    private String date;
    private String time;
    private String hiddenPhoneNumber;
    private String hiddenUserID;
    private Map<String, String> rOptions;
    private String owner;
    private String creator;
    private String target;

    private Dot dot;

    public void mainSubmitButton() {
        //TODO: add realisation of main button
    }

    public void hiddenSubmitButton() {
        //TODO: add realisation of hidden button
    }

    public void onOpen() {
        //todo: add realisation of onOpen event

        //     let getDots = {
        //         type: "getAllDots",
        //         ownerID: userID,
        //         ownerPhoneNumber: userPhone
        //     }
        //     socketSend(JSON.stringify(getDots));
    }

    public void sendDotsToOther() {
        //TODO: стандартно добавить реализацию
    }

    public void receiveMessage() {
        //TODO: реализовать забор данных с xhtml
    }

    public void doMessage(String message) {
        //TODO: реалтзовать отправку на js
    }


    private void handling(String message) throws IOException {
        doMessage(message);
    }



    private void rabbitSend(String message) {
        final String QUEUE_NAME = "fromClientToServerQueue";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] User sent '" + message + "'");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void rabbitReceive() {
        final String QUEUE_NAME = "fromServerToClientQueue";
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] User waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] User received '" + message + "'");
                handling(message);
            };
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }




    public Dot getDot() {
        return dot;
    }

    public void setDot(Dot dot) {
        this.dot = dot;
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

    public String getCanvasX() {
        return canvasX;
    }

    public void setCanvasX(String canvasX) {
        this.canvasX = canvasX;
    }

    public String getCanvasY() {
        return canvasY;
    }

    public void setCanvasY(String canvasY) {
        this.canvasY = canvasY;
    }

    public String getCanvasR() {
        return canvasR;
    }

    public void setCanvasR(String canvasY) {
        this.canvasR = canvasY;
    }

    public String[] getR() {
        return r;
    }

    public void setR(String[] r) {
        this.r = r;
    }

    public Map<String, String> getrOptions() {
        return rOptions;
    }

    public void setrOptions(Map<String, String> rOptions) {
        this.rOptions = rOptions;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHiddenPhoneNumber() {
        return hiddenPhoneNumber;
    }

    public void setHiddenPhoneNumber(String hiddenPhoneNumber) {
        this.hiddenPhoneNumber = hiddenPhoneNumber;
    }

    public String getHiddenUserID() {
        return hiddenUserID;
    }

    public void setHiddenUserID(String hiddenUserID) {
        this.hiddenUserID = hiddenUserID;
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

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
