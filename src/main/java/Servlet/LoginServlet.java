package Servlet;
import H2Codechecker;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

public class LoginServlet {



    private H2Codechecker h2Codechecker;

    public LoginServlet() {
    }

    public LoginServlet(H2Codechecker h2Codechecker) {
        this.h2Codechecker = h2Codechecker;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = user(request);
        if (user != null) {
            response.sendRedirect(response.encodeRedirectURL("/milestone"));
        } else {
            Map<String,Object> map = baseMap(request);
            showView(response, LOGIN_TEMPLATE, map);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String action = request.getParameter("submit");
        if (isEmpty(userName)) {
            issue(PLAIN_TEXT_UTF_8, HttpServletResponse.SC_BAD_REQUEST, "User name is not set", response);
        } else if (isEmpty(password)) {
            issue(PLAIN_TEXT_UTF_8, HttpServletResponse.SC_BAD_REQUEST, "Password is not set", response);
        } else if ("login".equals(action)) {
            if (!doLogin(request, userName, password)) {
                System.out.println("Failed login, return to login servlet");
                response.sendRedirect(response.encodeRedirectURL("/loginServlet"));
            }else{
                System.out.println("Logged in...");
                response.sendRedirect(response.encodeRedirectURL("/milestone"));
            }
        } else if ("register".equals(action)) {
            if (!doRegister(request, userName, password)) {
                System.out.println("Failed register, return to login servlet");
                response.sendRedirect(response.encodeRedirectURL("/milestone"));
            }else{
                response.sendRedirect(response.encodeRedirectURL("/confirmRegister.html"));
            }
        }
    }

    private boolean doLogin(HttpServletRequest request, String userName, String password) throws IOException {
        if (h2Codechecker.login(userName, password)) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", userName);
            return true;
        }
        return false;
    }


    private boolean doRegister(HttpServletRequest request, String userName, String password) throws IOException {
        //calls database to register user
        if(h2Codechecker.register(userName, password)){
            HttpSession session = request.getSession(true);
            session.setAttribute("user", userName);
            return true;
        }
        else{
            return false;
        }
    }

    private static boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }

}
