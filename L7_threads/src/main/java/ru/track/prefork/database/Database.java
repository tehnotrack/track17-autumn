package ru.track.prefork.database;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.prefork.Message;
import ru.track.prefork.database.exceptions.InvalidAuthor;

import java.sql.*;
import java.util.List;

public class Database implements ConversationService {
    private static final String[] DATABASES = {"tdb-1.trail5.net", "tdb-2.trail5.net", "tdb-3.trail5.net"};
    private static Logger logger = LoggerFactory.getLogger("logger");

    public static Connection getConnection(String author) throws InvalidAuthor, SQLException {
        String databaseUrl = getDatabase(author);

        String url = getConnectionUrl(databaseUrl);

        Connection connection = DriverManager.getConnection(url);

        logger.info("Connected to " + url);

        return connection;
    }

    @NotNull
    @Contract(pure = true)
    private static String getConnectionUrl(String databaseUrl) {

        return "jdbc:mysql://" +         //db type
                databaseUrl + ":" +      //host name
                "3306/" +                //port
                "track17?" +             //db name
                "user=track_student&" +  //login
                "password=7EsH.H6x";     // password
    }

    private static Connection getConnectionById(int id) throws SQLException {
        String url = DATABASES[id + 1];

        Connection connection = DriverManager.getConnection(url);

        logger.info("Connected to " + url);

        return connection;
    }

    private static String getDatabase(String author) throws InvalidAuthor {
        char authorFirstLetter = Character.toLowerCase(author.charAt(0));

        String database;

        if (authorFirstLetter >= 'a' && authorFirstLetter <= 'j') {
            database = DATABASES[0];
        } else if (authorFirstLetter >= 'k' && authorFirstLetter <= 't') {
            database = DATABASES[1];
        } else if (authorFirstLetter >= 'u' && authorFirstLetter <= 'z') {
            database = DATABASES[2];
        } else {
            throw new InvalidAuthor("First letter of author must be a letter (from 'a' to 'z')");
        }

        return database;
    }

    private static void save(Connection connection, String message, String author) throws SQLException {
        connection.setAutoCommit(false);

        String query = "INSERT INTO messages (user_name, text, ts) VALUES (?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(query);

        statement.setString(1, author);
        statement.setString(2, message);
        statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

        statement.execute();
        connection.commit();

        logger.info("Saved into database message: " + message + ", author: " + author);

        statement.close();
    }

    @Override
    public long store(Message msg) {
        String author = msg.getUsername();

        try {
            Connection connection = getConnection(author);

            save(connection, msg.getText(), author);
        } catch (InvalidAuthor | SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public List<Message> getHistory(long from, long to, long limit) {
        return null;
    }

    @Override
    public List<Message> getByUser(String username, long limit) {
        return null;
    }
}
