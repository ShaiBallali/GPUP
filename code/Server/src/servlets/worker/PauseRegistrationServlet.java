package servlets.worker;

import constants.Constants;
import engine.managers.users.WorkerManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Pause registration", urlPatterns = "/pause-registration")
public class PauseRegistrationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");

        WorkerManager workerManager = ServletUtils.getWorkerManager(getServletContext());
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
                    synchronized (ServletUtils.getRegistrationLocked()) {
                        workerManager.pauseRegistration(usernameFromSession, executionNameFromParameter.toLowerCase());
                    }
                    out.println("successful to pause registration.");
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
        } catch (Exception e) {
            String errorMessage = "PauseRegistrationServlet -> Error occurred. Message: " + e.getMessage();
            out.println(errorMessage);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
