package servlets.admin;

import com.google.gson.Gson;
import configuration.GsonConfig;
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

@WebServlet(name = "Create-graphviz", urlPatterns = "/create-graphviz")
public class GraphvizServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");

        MainManager mainManager = ServletUtils.getMainManager(getServletContext());
        String usernameFromSession = SessionUtils.getUsername(request);

        PrintWriter out = response.getWriter();

        try {
            if (usernameFromSession == null) {
                throw new Exception("You must first log in.");
            } else {
                String graphNameFromParameter = request.getParameter(Constants.GRAPH_NAME);
                String directoryPathFromParameters = request.getParameter(Constants.DIRECTORY_PATH);
                String fileNameFromParameters = request.getParameter(Constants.FILE_NAME);

                if (directoryPathFromParameters == null || directoryPathFromParameters.isEmpty() ||
                        fileNameFromParameters == null || fileNameFromParameters.isEmpty() ||
                        graphNameFromParameter == null || graphNameFromParameter.isEmpty()) {
                    throw new Exception("You must provide all parameters.");
                }

                mainManager.createGraphviz(graphNameFromParameter, directoryPathFromParameters, fileNameFromParameters);
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            String errorMessage = "GraphvizServlet -> Error occurred. Message: " + e.getMessage();
            out.println(errorMessage);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }


}
