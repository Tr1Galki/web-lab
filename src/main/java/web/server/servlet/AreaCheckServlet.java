package web.server.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.apache.commons.lang3.math.NumberUtils;
import web.server.Dot;
import web.server.data_base.DBHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "AreaCheckServlet", value = "/AreaCheckServlet")
public class AreaCheckServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String xStr = request.getParameter("x");
        String yStr = request.getParameter("y");
        String rStr = request.getParameter("r");
        String startTime = request.getParameter("startTime");
        String owner = request.getParameter("ownerID");
        String jsonResponse;

        //TODO: добавить всякие обработки пользователя, бд и т.п.

        boolean isValid = isCorrectX(xStr) && isCorrectY(yStr) && isCorrectR(rStr);
        if (isValid) {
            boolean isInArea = isInArea(xStr, yStr, rStr);
            jsonResponse = makeCorrectResponse(isInArea, xStr, yStr, rStr, startTime, owner);
        } else {
            jsonResponse = makeIncorrectResponse();
        }

        System.out.println(jsonResponse);

        response.setContentType("text/plыain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }

    private String makeCorrectResponse(boolean isInArea, String x, String y, String r, String startTime, String owner) {
        Date date = new Date(System.currentTimeMillis());
        long time = System.currentTimeMillis() - Long.parseLong(startTime);

        Dot dot = new Dot(isInArea, Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(r),
                date, (int) time, owner);

        //TODO: добавить обработку DB
        DBHandler dbHandler = new DBHandler();
        dbHandler.addDot(dot);

        Gson gson = new Gson();
        return gson.toJson(dot);
    }

    private String makeIncorrectResponse() {
        Map<String, String> map = new HashMap<>();
        map.put("isCorrect", "false");

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(map);
    }

    private boolean isInArea(String xStr, String yStr, String rStr) {
        double x = Double.parseDouble(xStr);
        double y = Double.parseDouble(yStr);
        double r = Double.parseDouble(rStr);

        if (x >= 0 && y >= 0) {
            return isInSquare(x, y, r);
        } else if ((x >= 0 && y <= 0)) {
            return isInCircle(x, y, r);
        } else if (x <= 0 && y <= 0) {
            return isInTriangle(x, y, r);
        }

        return false;
    }

    private boolean isInCircle(double x, double y, double r) {
        return Math.sqrt(x * x + y * y) <= r / 2;
    }

    private boolean isInSquare(double x, double y, double r) {
        return x < r && y < r;
    }

    private boolean isInTriangle(double x, double y, double r) {
        return y > -2 * x - r;
    }

    private boolean isCorrectX(String x) {
        return NumberUtils.isParsable(x.trim());
    }

    private boolean isCorrectY(String y) {
        return NumberUtils.isParsable(y.trim());
    }

    private boolean isCorrectR(String r) {
        return NumberUtils.isParsable(r.trim());
    }
}
