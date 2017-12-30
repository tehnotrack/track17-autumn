package examples;

import handlers.ResultHandler;

import java.sql.*;
import java.util.UUID;

import executor.SimpleExecutor;

public class ConnectWithStatements {
    static class ResultHandlerGetName implements ResultHandler {
        public void handle(ResultSet result) throws SQLException {
            result.next();
            System.out.append("Read user: " + result.getString("user_name") + '\n');
        }
    }

    public static void connect() {
        Connection connection = SimpleExample.getConnection();
        SimpleExecutor exec = new SimpleExecutor();
        try {
            String uuid = UUID.randomUUID().toString();
            //String query = "insert into users (user_name) values ('" + uuid + "')";
            //Statement stmt = connection.createStatement();
            String insert = "insert into users (user_name) values (?)";
            PreparedStatement preparedStmt = connection.prepareStatement(insert);
            preparedStmt.setString(1, uuid);
            //preparedStmt.execute();

            //ResultSet generatedKeys = preparedStmt.getGeneratedKeys();

            PreparedStatement select = connection.prepareStatement("SELECT * FROM users");// WHERE id=?");
            //select.setLong(1, 17L);
            ResultSet resultSet = select.executeQuery();
            System.out.println("result for:" + select.toString());
            while (resultSet.next()){
                long id = resultSet.getLong(1);
                String userName = resultSet.getString(2);
                System.out.println(String.format("%d: %s", id, userName));
            }
            //stmt.execute(query);
            //stmt.close();
            //exec.execUpdate(connection, "insert into users (user_name) values ('" + uuid + "')");
            System.out.append("User added\n");

            ResultHandler handler = new ResultHandlerGetName();
            exec.execQuery(connection, "select user_name from users where id=1", handler);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                exec.execUpdate(connection, "drop table users");
                System.out.append("Done!\n");

                connection.close();
            } catch (Exception ignore) {
            }
        }
    }


}
