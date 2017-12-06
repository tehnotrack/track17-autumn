package examples;

import handlers.ResultHandler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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

            exec.execUpdate(connection, "insert into users (user_name) values ('" + uuid + "')");
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
