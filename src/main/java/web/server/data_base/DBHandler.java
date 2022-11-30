package web.server.data_base;

import web.server.Dot;

import java.util.List;
import java.util.Locale;

public class DBHandler {

    public void addDot(Dot dot) {
        String values = String.format(Locale.US, " (%f, %f, %f, '%s', %d, '%s', %b) ",
                dot.getX(), dot.getY(), dot.getR(), dot.getStringDate(), dot.getTime(), dot.getOwner(), dot.getInArea());
        String INSERT_DOTS_SQL =
                "INSERT INTO dots (x, y, r, date, time, owner, area) " +
                        "VALUES " + values;
        DBConnector.SQLExecutor(INSERT_DOTS_SQL);
    }

    public void addUserIfNotExist(String ownerID, String number) {
        String params = String.format(Locale.US, " (%s, %s) ", ownerID, number);
        String INSERT_USER_SQL =
                "BEGIN " +
                        "IF NOT EXISTS (SELECT * FROM users " +
                        "WHERE id = " + ownerID + ") " +
                        "BEGIN " +
                        "INSERT INTO users (id, phone) " +
                        "VALUES" + params +
                        "END " +
                        "END";
        DBConnector.SQLExecutor(INSERT_USER_SQL);
    }

    public List<Dot> getDotsByUser(String number) {
        String SELECT_DOTS_BY_USER = "SELECT * FROM dots " +
                "WHERE owner = '" + number + "'";
        return DBConnector.SQLSelector(SELECT_DOTS_BY_USER);
    }
}
