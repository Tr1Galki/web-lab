package web.server.data_base;

import web.server.Dot;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class DBConnector {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "QAZwsx5709as";

    public static void SQLExecutor(String SQL_STATEMENT) {

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_STATEMENT)) {

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public static List<Dot> SQLSelector(String SQL_STATEMENT) {
        List<Dot> list = new LinkedList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_STATEMENT)) {

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Boolean isArea = rs.getBoolean("area");
                Double x = rs.getDouble("x");
                Double y = rs.getDouble("y");
                Double r = rs.getDouble("r");
                String date = rs.getString("date");
                Integer time = rs.getInt("time");
                String owner = rs.getString("owner");
                DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZ yyyy", Locale.ENGLISH);
                Date newDate = format.parse(date);
                Dot dot = new Dot(isArea, x, y, r, newDate, time, owner);
                list.add(dot);
            }

        } catch (SQLException e) {
            printSQLException(e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
