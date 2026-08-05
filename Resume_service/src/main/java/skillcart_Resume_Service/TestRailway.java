package skillcart_Resume_Service;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestRailway {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:postgresql://thomas.proxy.rlwy.net:31845/railway?sslmode=require";
        String user = "postgres";
        String pass = "YOUR_PASSWORD";

        System.out.println(pass.length());

        Connection connection =
                DriverManager.getConnection(url, user, pass);

        System.out.println("CONNECTED SUCCESSFULLY");
    }
}