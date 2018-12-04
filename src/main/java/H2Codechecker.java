import java.io.IOException;
import java.sql.*;
import java.util.*;
import Login.PasswordHash;

public class H2Codechecker implements AutoCloseable {

    public static final String MEMORY = "jdbc:h2:mem:shop";
    public static final String FILE = "jdbc:h2:~/Milestones";

    private Connection connection;

    static Connection getConnection(String db) throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");  // ensure the driver class is loaded when the DriverManager looks for an installed class. Idiom.
        return DriverManager.getConnection(db, "sa", "");  // default password, ok for embedded.
    }

    public H2Codechecker() {

        this(FILE);
    }

    public H2Codechecker(String db) {

        try {

            connection = getConnection(db);
            loadResource("/milestones.sql");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }





    public synchronized boolean register(final String userName, final String password) {
        errIfClosed();
        try {
            return registerSQL(userName, password);
        } catch (SQLException e) {

            return false;
        }
    }


    private boolean registerSQL(String userName, String password) throws SQLException {

        String hash = hash(password);

        if (hash == null) {
            return false;
        }
        if (hasUserSQL(userName)) {
            return false;
        }
        String query = "INSERT into users (username, password) VALUES(?,?)";
        //Test query to check users in the database
        //Will delete later
        String query2 = "SELECT * FROM users";

        PreparedStatement ps2 = connection.prepareStatement(query2);
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, userName);
            ps.setString(2, hash);
            int count = ps.executeUpdate();

            //test query for DB
            ResultSet test = ps2.executeQuery();
            while (test.next()) {
                System.out.println("This is the username: " + test.getString(1));
                System.out.println("This is the hashed password: " +test.getString(2));
            }

            return count == 1;

        }

    }




    boolean hasUserSQL(String userName) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT username FROM users WHERE username = ?")) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }




    public synchronized boolean login(final String userName, final String password) {
        errIfClosed();

        try {
            return loginSQL(userName, password);
        } catch (SQLException e) {

            return false;
        }
    }


    private boolean loginSQL(String userName, String password) throws SQLException {

        try (PreparedStatement ps = connection.prepareStatement("SELECT password FROM users WHERE username = ?")) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();


            if (rs.next()) {

                String hash = rs.getString("password");
                return (hash == null) ? false : validate(password, hash);
            }
        }
        return false;
    }


    private boolean validate(String password, String hash) {
        try {
            return PasswordHash.validatePassword(password, hash);
        } catch (PasswordHash.PasswordException e) {

            return false;
        }
    }


    private String hash(String password) {
        try {
            return PasswordHash.createHash(password);
        } catch (PasswordHash.PasswordException e) {

            return null;
        }
    }

    private void loadResource(String name) {
        try {

            String cmd = new Scanner(getClass().getResource(name).openStream()).useDelimiter("\\Z").next();

            PreparedStatement ps = connection.prepareStatement(cmd);
            ps.execute();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void errIfClosed() {
        if (connection == null) {
            throw new NullPointerException("H2 connection is closed");
        }
    }





}



