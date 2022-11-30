package web.server.endpoint;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import web.server.QueueHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.*;

@ServerEndpoint("/web-socket")
public class RabbitWebSocket {

    private static final ConcurrentHashMap<Session, String> map = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println(session);
        map.put(session, "");
        init();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        String ownerPhoneNumber = getOwnerPhone(message);

        System.out.println("------");
        System.out.println(map);
        map.put(session, ownerPhoneNumber);
        System.out.println(map);
        rabbitSend(message);
    }

    @OnClose
    public void onClose(Session session) {
        map.remove(session);
        System.out.println("On close triggered with session: " + session);
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        map.remove(session);
        System.out.println("On close triggered");
        System.out.println(thr.toString());
    }

    private void doMessage(String message) {
        String ownerPhoneNumber = getOwnerPhone(message);

        for (Map.Entry<Session, String> entry : map.entrySet()) {
            Session key = entry.getKey();
            String value = entry.getValue();
            if (value.equals(ownerPhoneNumber)) {
                try {
                    System.out.println("[sending]");
                    System.out.println(map);
                    System.out.println(value);
                    key.getAsyncRemote().sendText(message);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        //        queue.peek().getAsyncRemote().sendText("your session: " + queue.peek().toString() + " message: " + message);
    }

    private void handling(String message) throws IOException {
        doMessage(message);
    }

    private void init() {
        QueueHandler.run();
        rabbitReceive();
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

    private String getOwnerPhone(String message) {
        JSONObject json = new JSONObject(message);
        return json.get("ownerPhoneNumber").toString();
    }
}
