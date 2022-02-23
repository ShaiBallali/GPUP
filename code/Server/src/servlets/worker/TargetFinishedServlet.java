package servlets.worker;

import configuration.GsonConfig;
import dto.dtoServer.worker.worker2server.RunningResultDetails;
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
import java.util.Properties;

@WebServlet(name = "Target finished", urlPatterns = "/target-finished")
public class TargetFinishedServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        MainManager mainManager = ServletUtils.getMainManager(getServletContext());
        WorkerManager workerManager = ServletUtils.getWorkerManager(getServletContext());
        String usernameFromSession = SessionUtils.getUsername(request);

        PrintWriter out = response.getWriter();

        try {
            if (usernameFromSession == null) {
                throw new Exception("You must first log in.");
            } else {
                Properties prop = new Properties();
                prop.load(request.getInputStream());
                String runningResultDetailsJson = prop.getProperty("runningResultDetails");

                RunningResultDetails runningResultDetails = GsonConfig.gson.fromJson(runningResultDetailsJson, RunningResultDetails.class);

                if (runningResultDetails == null) {
                    throw new Exception("Invalid JSON.");
                }

                synchronized (ServletUtils.getRunningTargetLocked()) {
                    mainManager.onTargetFinishedToRun(runningResultDetails);
                    int price = mainManager.getPrice(runningResultDetails.getExecutionName());
                    workerManager.addToCredit(usernameFromSession, price);
                }
                out.println("update regarding target who finished running was successful");
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            String errorMessage = "TargetFinishedServlet -> Error occurred. Message: " + e.getMessage();
            out.println(errorMessage);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}

