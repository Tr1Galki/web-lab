package web.server;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.json.JSONObject;
import web.server.data_base.DBHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class QueueHandler {



    public static void run() {
        receive();
    }

    private static void receive() {
        final String QUEUE_NAME = "fromClientToServerQueue";
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Server waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Server received '" + message + "'");
                handling(message);
            };
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            System.out.println("server error: ");
            e.printStackTrace();
        }
    }

    private static void handling(String jsonMessage) {
//        JSONObject json = new JSONObject(jsonMessage);
//        String ownerNumber = json.get("ownerNumber").toString();
//
//        Gson gson = new Gson();
//        String jsonResponse = "{\"type\": \"getDotsByDB\", \"owner\": \"" + ownerNumber + "\"}";
//
//        System.out.println(jsonResponse);
//        send(jsonResponse);


        Gson gson = new Gson();
        JSONObject json = new JSONObject(jsonMessage);
        String type = json.get("type").toString();
        switch (type) {
            case ("getAllDots"): {
                String ownerPhone = json.get("ownerPhoneNumber").toString();
                String ownerID = json.get("ownerID").toString();
                DBHandler dbHandler = new DBHandler();

                dbHandler.addUserIfNotExist(ownerID, ownerPhone);

                List<Dot> list = dbHandler.getDotsByUser(ownerPhone);
                String jsonResponse = gson.toJson(list);
                jsonResponse = "{\"type\":\"allDots\", \"array\": " + jsonResponse + "}";

                send(jsonResponse);
                break;
            }
            default: {
                break;
            }
        }
    }

    private static void send(String message) {
        final String QUEUE_NAME = "fromServerToClientQueue";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Server sent '" + message + "'");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
