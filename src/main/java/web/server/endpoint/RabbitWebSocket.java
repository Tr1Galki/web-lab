package web.server.endpoint;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import web.server.QueueHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

@ServerEndpoint("/web-socket")
public class RabbitWebSocket {
    private Session session;
    private static Queue<Session> queue = new ConcurrentLinkedQueue<>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("---------------------------------------------------------");
        queue.add(session);
        System.out.println(this.session);
        init();
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
        System.out.println(message);
        rabbitSend(message);
    }

    @OnClose
    public void onClose(Session session) {
        queue.remove(session);
        System.out.println("On close triggered with session: " + session);
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        queue.remove(session);
        System.out.println("On close triggered");
        System.out.println(thr.toString());
    }

    private void doMessage(String message) {
        System.out.println("send to JS: " + message);
        assert queue.peek() != null;
        queue.peek().getAsyncRemote().sendText("your session: " + queue.peek().toString() + " message: " + message);
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
}