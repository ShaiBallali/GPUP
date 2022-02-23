package servlets.admin;


import com.google.gson.Gson;
import configuration.GsonConfig;
import dto.dtoServer.execution.ExecutionList;
import engine.managers.MainManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Execution list", urlPatterns = "/execution-list")
public class ExecutionListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        MainManager mainManager = ServletUtils.getMainManager(getServletContext());
        String usernameFromSession = SessionUtils.getUsername(request);

        try {
            if (usernameFromSession == null) {
                throw new Exception("You must first log in.");
            } else {
                Gson gson = GsonConfig.gson;
                ExecutionList executionList = (ExecutionList) mainManager.getExecutionList();
                String json = gson.toJson(executionList);
                out.println(json);
                out.flush();
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            String errorMessage = "ExecutionListServlet -> Error occurred. Message: " + e.getMessage();
            out.println(errorMessage);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
