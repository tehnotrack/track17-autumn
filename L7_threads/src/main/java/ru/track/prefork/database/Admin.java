package ru.track.prefork.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Admin {
    public static void main(String[] args) throws SQLException {
        System.out.println(args[0]);

        Connection connection = Database.getConnectionById(Integer.parseInt(args[0]));

        String query = "SELECT * FROM messages";// ORDER BY ts DESC limit 10";

        PreparedStatement statement = connection.prepareStatement(query);

        statement.execute();
        connection.close();
    }
}
