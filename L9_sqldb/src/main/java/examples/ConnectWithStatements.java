package examples;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import executor.SimpleExecutor;
import handlers.ResultHandler;

public class ConnectWithStatements {

    static class ResultHandlerGetName implements ResultHandler {
        public void handle(ResultSet result) throws SQLException {
            result.next();
            System.out.append("Read user: " + result.getString("user_name") + '\n');
        }
    }

    public static void main(String[] args) {
        connect();
    }

    public static void connect() {
        Connection connection = SimpleExample.getConnection();
        SimpleExecutor exec = new SimpleExecutor();
        try {
            String uuid = UUID.randomUUID().toString();

            // 1
//            String query = "INSERT INTO users (user_name) VALUES ('" + uuid + "')";
//            Statement stmt = connection.createStatement();
//            stmt.execute(query);
//            stmt.close();

            //2 INSERT
            /*
            INSERT INTO messages (user_name, text, ts) VALUES ('myusername', 'helloworld', now())
            now() <- System.currentTimeMillis()

             */
            String insert = "INSERT INTO users(user_name) values(?)";
            PreparedStatement preparedStmt = connection.prepareStatement(
                    insert, Statement.RETURN_GENERATED_KEYS);
            preparedStmt.setString(1, "hello_" + uuid);
            preparedStmt.execute();


            ResultSet generatedKeys = preparedStmt.getGeneratedKeys();
            generatedKeys.next();
            Long createdId = generatedKeys.getLong(1);
            System.out.println("Result of: " + preparedStmt.toString() + "\n" + createdId);



            // SELECT
            /*
            select * from messages order by ts desc limit 10;
             */
            PreparedStatement select = connection.prepareStatement(
                    "SELECT * FROM users WHERE id=?");
            select.setLong(1, 17L);
            ResultSet resultSet = select.executeQuery();

            System.out.println("result for: " + select.toString());
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String userName = resultSet.getString("user_name");

                System.out.print(String.format("%d:%s\n", id, userName));
            }


//            exec.execUpdate(connection, "INSERT INTO users (user_name) VALUES ('" + uuid + "')");
//            System.out.append("User added\n");

//            ResultHandler handler = new ResultHandlerGetName();
//            exec.execQuery(connection, "select user_name from users where id=1", handler);

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