package servlets.general;

import constants.Constants;
import engine.managers.users.UserManager;
import engine.managers.users.WorkerManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

@WebServlet(name = "Login", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        WorkerManager workerManager = ServletUtils.getWorkerManager(getServletContext());
        String usernameFromSession = SessionUtils.getUsername(request);

        if (usernameFromSession == null) { //user is not logged in yet

            String usernameFromParameter = request.getParameter(Constants.USERNAME);
            String userTypeFromParameter = request.getParameter(Constants.USER_TYPE);

            if (usernameFromParameter == null || usernameFromParameter.isEmpty() || userTypeFromParameter == null || userTypeFromParameter.isEmpty() ) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();
                userTypeFromParameter = userTypeFromParameter.trim();

                //These two actions needs to be considered atomic, and synchronizing only each one of them, solely, is not enough
                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";

                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getOutputStream().print(errorMessage);
                    }
                    else {
                        userManager.addUser(usernameFromParameter, userTypeFromParameter);
                        request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);

                        if (userTypeFromParameter.equals("WORKER")) {
                            workerManager.addWorker(usernameFromParameter);
                        }
                        System.out.println("On login, request URI is: " + request.getRequestURI());
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
