package web.server.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ControllerServlet", value = "/ControllerServlet", asyncSupported = true)
public class ControllerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestType = request.getParameter("requestType");

        switch (requestType) {
            case ("areaReq"): {
                areaRequest(request, response);
                break;
            }
            default: {
                break;
            }
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("text");
        out.flush();
    }

    private void areaRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = "/AreaCheckServlet";
        ServletContext servletContext = getServletContext();
        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(path);
        requestDispatcher.forward(request, response);
    }
}
