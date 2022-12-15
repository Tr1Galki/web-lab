package web.server;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.json.JSONArray;
import org.json.JSONObject;
import web.server.bean.Dot;
import web.server.data_base.DBHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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


        Gson gson = new Gson();
        JSONObject json = new JSONObject(jsonMessage);
        String type = json.get("type").toString();
        String ownerPhone = json.get("ownerPhoneNumber").toString();
        String ownerID = json.get("ownerID").toString();
        String jsonResponse;

        switch (type) {
            case ("getAllDots"): {
                DBHandler dbHandler = new DBHandler();
                dbHandler.addUserIfNotExist(ownerID, ownerPhone);

                List<Dot> list = dbHandler.getDotsByUser(ownerPhone);
                jsonResponse = "{" +
                        "\"type\":\"allDots\"," +
                        "\"ownerPhoneNumber\":\"" + ownerPhone + "\"," +
                        " \"array\": " + gson.toJson(list) +
                        "}";

                send(jsonResponse);
                break;
            }
            case ("sendDots"): {
                String targetPhoneNumber = json.get("targetPhoneNumber").toString();

                DBHandler dbHandler = new DBHandler();
                if (dbHandler.isUserExist(targetPhoneNumber)) {
                    String stringArray = json.get("array").toString();

                    String extraJsonResponse = "{" +
                            "\"type\":\"receivedDots\"," +
                            "\"ownerPhoneNumber\":\"" + targetPhoneNumber + "\"," +
                            "\"array\":" + stringArray +
                            "}";
                    jsonResponse = "{" +
                            "\"type\":\"sentDots\"," +
                            "\"ownerPhoneNumber\":\"" + ownerPhone + "\"," +
                            "\"result\":\"dotsAdded\"" +
                            "}";

                    JSONArray array = new JSONArray(stringArray);
                    LinkedList<Dot> linkedList = new LinkedList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject elem = new JSONObject(array.get(i).toString());
                        Dot dot = null;
                        try {
                            DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZ yyyy", Locale.ENGLISH);
                            Date newDate = format.parse(elem.getString("date"));
                            dot = new Dot(elem.getBoolean("inArea"), elem.getDouble("x"), elem.getDouble("y"),
                                    elem.getDouble("r"), newDate, elem.getInt("time"),
                                    targetPhoneNumber, elem.getString("creator"));
                        } catch (ParseException e) {
                            DateFormat format = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss a", Locale.ENGLISH);
                            Date newDate = null;
                            try {
                                newDate = format.parse(elem.getString("date"));
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            dot = new Dot(elem.getBoolean("inArea"), elem.getDouble("x"), elem.getDouble("y"),
                                    elem.getDouble("r"), newDate, elem.getInt("time"),
                                    targetPhoneNumber, elem.getString("creator"));
                        }
                        linkedList.add(dot);
                    }

                    dbHandler.sendToTarget(linkedList);
                    send(extraJsonResponse);

                } else {
                    jsonResponse = "{" +
                            "\"type\":\"sentDots\"," +
                            "\"ownerPhoneNumber\":\"" + ownerPhone + "\"," +
                            "\"result\":\"userNotExist\"" +
                            "}";
                }
                send(jsonResponse);
                break;
            }
            case ("checkAndAdd"): {
                Dot dot = new Dot(Boolean.parseBoolean(json.getJSONObject("dot").getString("inArea")),
                        Double.parseDouble(json.getJSONObject("dot").getString("x")),
                        Double.parseDouble(json.getJSONObject("dot").getString("y")),
                        Double.parseDouble(json.getJSONObject("dot").getString("r")), new Date(),
                        (int) System.currentTimeMillis() - Integer.parseInt(json.getJSONObject("dot").getString("time")),
                        ownerPhone, json.getJSONObject("dot").getString("creator"));
                DBHandler dbHandler = new DBHandler();
                dbHandler.addDot(dot);
                jsonResponse = "{" +
                        "\"type\":\"newDot\"," +
                        "\"ownerPhoneNumber\":\"" + ownerPhone + "\"," +
                        "\"dot\":" + dot.getJson() + "" +
                        "}";
                send(jsonResponse);
                break;
            }
            default: {
                break;
            }
        }
    }

    private static void send(String message) {
        final String EXCHANGE_NAME = "fromServerToClientQueue";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));

            System.out.println(" [x] Server sent '" + message + "'");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
