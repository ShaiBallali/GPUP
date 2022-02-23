import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "Hello", urlPatterns = "/hello")
public class helloWorld extends HttpServlet {
    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().println("Hello World !");
    }
}