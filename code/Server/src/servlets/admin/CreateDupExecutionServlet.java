package servlets.admin;

import configuration.GsonConfig;
import dto.dtoServer.execution.DupExecutionDetails;
import engine.managers.MainManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

@WebServlet(name = "Create duplicate execution", urlPatterns = "/create-dup-execution")
public class CreateDupExecutionServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        MainManager mainManager = ServletUtils.getMainManager(getServletContext());
        String usernameFromSession = SessionUtils.getUsername(request);

        PrintWriter out = response.getWriter();

        try {
            if (usernameFromSession == null) {
               throw new Exception("You must first log in.");
            } else {
                Properties prop = new Properties();
                prop.load(request.getInputStream());
                String jsonDupExecutionDetails = prop.getProperty("dupExecutionDetails");
                DupExecutionDetails dupExecutionDetails = GsonConfig.gson.fromJson(jsonDupExecutionDetails, DupExecutionDetails.class);

                if (dupExecutionDetails == null) {
                    throw new Exception("Invalid JSON.");
                }

                dupExecutionDetails.setCreatedBy(usernameFromSession);

                mainManager.duplicateExecution(dupExecutionDetails);

                out.println("Execution creation successfully performed.");
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
                String errorMessage = "CreateDupExecutionServlet -> Error occurred. Message: " + e.getMessage();
                out.println(errorMessage);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }
