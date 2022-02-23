package servlets.worker;


import com.google.gson.Gson;
import configuration.GsonConfig;
import dto.dtoServer.worker.server2worker.RunTimeExecutionList;
import engine.managers.MainManager;
import engine.managers.users.WorkerManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Execution details worker servlet", urlPatterns = "/execution-details-worker-servlet")
public class ExecutionDetailsWorkerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        MainManager mainManager = ServletUtils.getMainManager(getServletContext());
        WorkerManager workerManager = ServletUtils.getWorkerManager(getServletContext());
        String usernameFromSession = SessionUtils.getUsername(request);

        try {
            if (usernameFromSession == null) {
                throw new Exception("You must first log in.");
            } else {
                RunTimeExecutionList runTimeExecutionList = workerManager.getRunTimeExecutionList(usernameFromSession, mainManager);
                Gson gson = GsonConfig.gson;
                String json = gson.toJson(runTimeExecutionList);
                out.println(json);
                out.flush();
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            String errorMessage = "ExecutionDetailsWorkerServlet -> Error occurred. Message: " + e.getMessage();
            out.println(errorMessage);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}