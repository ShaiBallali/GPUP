package servlets.admin;

import com.google.gson.Gson;
import configuration.GsonConfig;
import constants.Constants;
import dto.dtoServer.graphAction.CirclePath;
import engine.managers.MainManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Detect circle", urlPatterns = "/detect-circle")
public class DetectCircleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        MainManager mainManager = ServletUtils.getMainManager(getServletContext());
        String usernameFromSession = SessionUtils.getUsername(request);

        PrintWriter out = response.getWriter();

        try {
            if (usernameFromSession == null) {
                throw new Exception("You must first log in.");
            } else {
                String graphNameFromParameter = request.getParameter(Constants.GRAPH_NAME);
                String targetNameFromParameter = request.getParameter(Constants.TARGET_NAME);

                if (graphNameFromParameter == null || graphNameFromParameter.isEmpty() ||
                        targetNameFromParameter == null || targetNameFromParameter.isEmpty()) {
                    throw new Exception("You must provide all parameters.");
                } else {
                    CirclePath circlePath = (CirclePath) mainManager.detectCircle(graphNameFromParameter.toLowerCase(), targetNameFromParameter.toUpperCase());
                    Gson gson = GsonConfig.gson;
                    String json = gson.toJson(circlePath);
                    out.println(json);
                    out.flush();
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
        } catch (Exception e) {
            String errorMessage = "DetectCircleServlet -> Error occurred. Message: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.println(errorMessage);
        }
    }
}




