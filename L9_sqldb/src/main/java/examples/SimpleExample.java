package examples;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleExample {

    public static Connection getConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("tdb-2.trail5.net:").            //host name
                    append("3306/").                //port
                    append("track17?").            //db name
                    append("user=track_student&").            //login
                    append("password=7EsH.H6x");

           // url.
                    //append("jdbc:mysql://").        //db type
                    /*append("tdb-2.trail5.net:").            //host name
                    append("3306/").                //port
                    append("track17?").            //db name
                    append("user=track_student&").            //login
                    append("password=7EsH.H6x");

            url.
                    append("jdbc:mysql://").        //db type
                    append("tdb-3.trail5.net:").            //host name
                    append("3306/").                //port
                    append("track17?").            //db name
                    append("user=track_student&").            //login
                    append("password=7EsH.H6x");        //password*/
                                                                                    //password

            System.out.append("URL: " + url + "\n");

            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void connect() {
        Connection connection = getConnection();
        System.out.append("Connected!\n");
        try {
            System.out.append("Autocommit: " + connection.getAutoCommit() + '\n');
            System.out.append("DB name: " + connection.getMetaData().getDatabaseProductName() + '\n');
            System.out.append("DB version: " + connection.getMetaData().getDatabaseProductVersion() + '\n');
            System.out.append("Driver name: " + connection.getMetaData().getDriverName() + '\n');
            System.out.append("Driver version: " + connection.getMetaData().getDriverVersion() + '\n');
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
