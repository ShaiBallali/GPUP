package servlets.admin;

import constants.Constants;
import engine.managers.MainManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Is valid name", urlPatterns = "/is-valid-name")

public class IsValidNameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        MainManager mainManager = ServletUtils.getMainManager(getServletContext());
        String usernameFromSession = SessionUtils.getUsername(request);

        PrintWriter out = response.getWriter();

        try {
            if (usernameFromSession == null) {
                throw new Exception("You must first log in.");
            } else {
                String executionNameFromParameter = request.getParameter(Constants.EXECUTION_NAME);

                if (executionNameFromParameter == null || executionNameFromParameter.isEmpty()) {
                    throw new Exception("You must provide all parameters.");
                } else {
                    boolean isValidName = mainManager.isValidName(executionNameFromParameter.toLowerCase());
                    if (isValidName) {
                        response.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        throw new Exception("The name: " + "\"" + executionNameFromParameter + "\"" + " is already used");
                    }
                }
            }
        } catch (Exception e) {
            String errorMessage = "IsValidNameServlet -> Error occurred. Message: " + e.getMessage();
            out.println(errorMessage);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
