package servlets.admin;

import com.google.gson.Gson;
import configuration.GsonConfig;
import constants.Constants;
import dto.dtoServer.graphAction.TargetsPaths;
import dto.enums.DependencyType;
import engine.managers.MainManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Find path", urlPatterns = "/find-path")
public class FindPathServlet extends HttpServlet {
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
                String graphNameFromParameter = request.getParameter(Constants.GRAPH_NAME);
                String srcNameFromParameter = request.getParameter(Constants.SRC_NAME);
                String dstNameFromParameter = request.getParameter(Constants.DST_NAME);
                String dependencyTypeFromParameter = request.getParameter(Constants.DEPENDENCY_TYPE);

                if (graphNameFromParameter == null || graphNameFromParameter.isEmpty() ||
                        srcNameFromParameter == null || srcNameFromParameter.isEmpty() ||
                        dstNameFromParameter == null || dstNameFromParameter.isEmpty() ||
                        dependencyTypeFromParameter == null || dependencyTypeFromParameter.isEmpty()) {
                    throw new Exception("You must provide all parameters.");
                } else {
                    TargetsPaths targetsPaths = (TargetsPaths) mainManager.findPaths(graphNameFromParameter.toLowerCase(), srcNameFromParameter.toUpperCase(), dstNameFromParameter.toUpperCase(), DependencyType.valueOf(dependencyTypeFromParameter.toUpperCase()));
                    Gson gson = GsonConfig.gson;
                    String json = gson.toJson(targetsPaths);
                    out.println(json);
                    out.flush();
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
        } catch (Exception e) {
            String errorMessage = "FindPathServlet -> Error occurred. Message: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.println(errorMessage);
        }
    }
}
