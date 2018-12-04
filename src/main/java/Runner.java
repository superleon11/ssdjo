import java.net.UnknownHostException;
import Servlet.*;

import java.net.UnknownHostException;


public class Runner {



        private static final int PORT = 9001;

        private Runner() throws UnknownHostException {




        }

        private void start() throws Exception {

            Server server = new Server(PORT);


        /*
        servlet handler controls the context, ie where web resources are located.
         */
            ServletContextHandler handler = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
            handler.setInitParameter("org.eclipse.jetty.servlet.Default." + "resourceBase", "SSDCourseworkJO/src/main/Web");

        /*
        Servlet to handle index page.
         */


            LoginServlet login = new LoginServlet();
            handler.addServlet(new ServletHolder(login), "/loginServlet");


        /*
        sets default servlet path.
         */
            DefaultServlet ds = new DefaultServlet();
            handler.addServlet(new ServletHolder(ds), "/");


        /*
        starts server
         */
            server.start();
            System.out.println("Server started, will run until terminated");
            server.join();
        }

        /*
        main program start here
        */
        public static void main(String[] args) {
            try {
                System.out.println("starting");
                new Runner().start();
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }


    }



