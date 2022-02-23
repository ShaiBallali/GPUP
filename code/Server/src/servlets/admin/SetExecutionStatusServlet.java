package servlets.admin;

import constants.Constants;
import dto.enums.ExecutionStatus;
import engine.managers.MainManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Set execution status", urlPatterns = "/set-execution-status")
public class SetExecutionStatusServlet extends HttpServlet {

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
                String statusFromParameter = request.getParameter(Constants.STATUS);

                if (statusFromParameter == null || statusFromParameter.isEmpty() || executionNameFromParameter == null || executionNameFromParameter.isEmpty()) {
                    throw new Exception("You must provide all parameters.");
                } else {
                    ExecutionStatus executionStatus = ExecutionStatus.valueOf(statusFromParameter);
                    synchronized (ServletUtils.getRunningTargetLocked()) {
                        mainManager.setExecutionStatus(executionNameFromParameter, executionStatus);
                    }
                    String message = "Success to change execution status.";
                    out.println(message);
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
        } catch (Exception e) {
            String errorMessage = "SetExecutionStatusServlet -> Error occurred. Message: " + e.getMessage();
            out.println(errorMessage);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
