package servlets.admin;

import com.google.gson.Gson;
import configuration.GsonConfig;
import dto.dtoServer.graph.GraphList;
import engine.managers.MainManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Graph list", urlPatterns = "/graph-list")
public class GraphListServlet extends HttpServlet {
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
                Gson gson = GsonConfig.gson;
                GraphList graphList = (GraphList) mainManager.getGraphList();
                String json = gson.toJson(graphList);
                out.println(json);
                out.flush();
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            String errorMessage = "GraphListServlet -> Error occurred. Message: " + e.getMessage();
            out.println(errorMessage);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
