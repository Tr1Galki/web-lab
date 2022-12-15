package web.server.bean;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
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
        rOptions = new LinkedHashMap<String, String>();
        rOptions.put("1", "1");
        rOptions.put("1.5", "1.5");
        rOptions.put("2", "2");
        rOptions.put("2.5", "2.5");
        rOptions.put("3", "3");
    }

    @Inject
    @Push(channel="incoming")
    private PushContext incoming;

    @EJB
    private MessageService msgService;

    private String x;
    private String y;
    private String[] r;
    private Map<String, String> rOptions;
    private String owner;
    private String creator;

    private Dot dot;


    private String enteredMessage;

    public List<String> getMessages() {
        return msgService.getMessages();
    }

    public void receiveMessage() {;
        //TODO: реализовать забор данных с xhtml
    }

    public void doMessage(String message) {
        System.out.println("send-start");
        incoming.send("dot" );
        System.out.println("send-stop");
        System.out.println("---");
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

    public String getEnteredMessage() {
        return enteredMessage;
    }

    public void setEnteredMessage(String enteredMessage) {
        this.enteredMessage = enteredMessage;
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
