package web.server.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import web.server.TEST;

import java.io.IOException;

@WebServlet(name = "RabbitMakerServlet", value = "/RabbitMakerServlet")
public class RabbitMakerServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            TEST.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
