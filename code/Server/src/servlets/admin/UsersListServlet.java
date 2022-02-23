package servlets.admin;

import com.google.gson.Gson;
import configuration.GsonConfig;

import dto.dtoServer.users.UsersList;
import engine.managers.users.UserManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;


import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "UsersList", urlPatterns = "/userslist")
public class UsersListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        try {
            Gson gson = GsonConfig.gson;
            UserManager userManager = ServletUtils.getUserManager(request.getServletContext());

            UsersList usersList = userManager.getUsers();
            String json = gson.toJson(usersList);
            out.println(json);
            out.flush();
        } catch (Exception e) {
            String errorMessage = "UsersListServlet -> Error occurred. Message: " + e.getMessage();
            out.println(errorMessage);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}

