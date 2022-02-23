package servlets.worker;

import com.google.gson.Gson;
import configuration.GsonConfig;
import constants.Constants;
import dto.dtoServer.worker.server2worker.ExecutableTargets;
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

@WebServlet(name = "Get executable targets", urlPatterns = "/get-executable-targets")
public class GetExecutableTargetsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        MainManager mainManager = ServletUtils.getMainManager(getServletContext());
        WorkerManager workerManager = ServletUtils.getWorkerManager(getServletContext());
        String usernameFromSession = SessionUtils.getUsername(request);

        PrintWriter out = response.getWriter();

        try {
            if (usernameFromSession == null) {
                throw new Exception("You must first log in.");
            } else {
                String targetsAmountFromParameter = request.getParameter(Constants.TARGETS_AMOUNT);

                if (targetsAmountFromParameter == null || targetsAmountFromParameter.isEmpty()) {
                    throw new Exception("You must provide all parameters.");
                } else {
                    ExecutableTargets executableTargets;
                    synchronized (ServletUtils.getRunningTargetLocked()) {
                        int targetAmount = Integer.parseInt(targetsAmountFromParameter);
                        executableTargets = workerManager.getTargets(usernameFromSession, targetAmount, mainManager);
                    }
                    Gson gson = GsonConfig.gson;
                    String json = gson.toJson(executableTargets);
                    out.println(json);
                    out.flush();

                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
        } catch (Exception e) {
            String errorMessage = "GetExecutableTargetsServlet -> Error occurred. Message: " + e.getMessage();
            out.println(errorMessage);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
