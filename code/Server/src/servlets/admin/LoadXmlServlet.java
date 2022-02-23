package servlets.admin;

import engine.managers.MainManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;


@WebServlet(name = "Upload file", urlPatterns = "/upload-file")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoadXmlServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        MainManager mainManager = ServletUtils.getMainManager(getServletContext());
        String usernameFromSession = SessionUtils.getUsername(request);

        PrintWriter out = response.getWriter();

        try {
            if (usernameFromSession == null) {
                throw new Exception("You must first log in.");
            } else {
                Collection<Part> parts = request.getParts();
                out.println("Total parts: " + parts.size());

                for (Part part : parts) {
                    mainManager.loadGraph(usernameFromSession, part.getInputStream());
                    out.println("Load successfully.");
                    response.setStatus(HttpServletResponse.SC_OK);
                }
            }
        } catch (Exception e) {
            String errorMessage = "LoadXmlServlet -> Error occurred. Message: " + e.getMessage();
            out.println(errorMessage);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}

