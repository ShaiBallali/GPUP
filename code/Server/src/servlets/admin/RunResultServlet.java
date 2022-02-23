package servlets.admin;

import com.google.gson.Gson;
import configuration.GsonConfig;
import constants.Constants;
import dto.dtoServer.result.TaskResult;
import engine.managers.MainManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Run result", urlPatterns = "/run-result")
public class RunResultServlet extends HttpServlet {
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
                String executionNameFromParameter = request.getParameter(Constants.EXECUTION_NAME);

                if (executionNameFromParameter == null || executionNameFromParameter.isEmpty()) {
                    throw new Exception("You must provide all parameters.");
                } else {
                    TaskResult taskResult = mainManager.getTaskResult(executionNameFromParameter.toLowerCase());
                    Gson gson = GsonConfig.gson;
                    String json = gson.toJson(taskResult);
                    out.println(json);
                    out.flush();
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
        } catch (Exception e) {
            String errorMessage = "RunResultServlet -> Error occurred. Message: " + e.getMessage();
            out.println(errorMessage);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
