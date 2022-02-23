package servlets.admin;

import com.google.gson.Gson;
import configuration.GsonConfig;
import constants.Constants;
import dto.dtoServer.graphAction.WhatIfDetails;
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

@WebServlet(name = "What if", urlPatterns = "/what-if")
public class WhatIfServlet extends HttpServlet {
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
                String targetNameFromParameter = request.getParameter(Constants.TARGET_NAME);
                String dependencyTypeFromParameter = request.getParameter(Constants.DEPENDENCY_TYPE);

                if (graphNameFromParameter == null || graphNameFromParameter.isEmpty() ||
                        targetNameFromParameter == null || targetNameFromParameter.isEmpty() ||
                        dependencyTypeFromParameter == null || dependencyTypeFromParameter.isEmpty()) {
                    throw new Exception("You must provide all parameters.");
                } else {
                    WhatIfDetails whatIfDetails = (WhatIfDetails) mainManager.findWhatIfTargets(graphNameFromParameter.toLowerCase(), targetNameFromParameter.toUpperCase(), DependencyType.valueOf(dependencyTypeFromParameter.toUpperCase()));
                    Gson gson = GsonConfig.gson;
                    String json = gson.toJson(whatIfDetails);
                    out.println(json);
                    out.flush();
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
        } catch (Exception e) {
            String errorMessage = "WhatIfServlet -> Error occurred. Message: " + e.getMessage();
            out.println(errorMessage);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}

