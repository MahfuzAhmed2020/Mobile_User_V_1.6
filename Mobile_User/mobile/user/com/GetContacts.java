package mobile.user.com;

import java.sql.*;

public class GetContacts {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/contact_app";
        String user = "root";
        String password = "";

        try {
            // Load Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect
            Connection conn = DriverManager.getConnection(url, user, password);

            // Query
            String sql = "SELECT firstName, lastName, phone, email FROM contacts";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Read Data
            while (rs.next()) {
                System.out.println(
                    rs.getString("firstName") + " | " +
                    rs.getString("lastName") + " | " +
                    rs.getString("phone") + " | " +
                    rs.getString("email")
                );
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
