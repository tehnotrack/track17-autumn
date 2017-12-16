package ru.track.prefork.database;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.prefork.Message;
import ru.track.prefork.database.exceptions.InvalidAuthor;

import java.sql.*;
import java.util.*;

public class Database implements ConversationService {
    private static final String[]                DATABASES   = {"tdb-1.trail5.net", "tdb-2.trail5.net", "tdb-3.trail5.net"};
    private static       Logger                  logger      = LoggerFactory.getLogger("logger");
    private static       Map<String, Connection> connections = new HashMap<>();
    
    public Database() {
        for (String database : DATABASES) {
            Connection connection = getConnectionByDbUrl(database);
            
            if (connection != null) {
                connections.put(database, connection);
            }
        }
    }
    
    private static Connection getConnectionByUsername(String username) throws InvalidAuthor, SQLException {
        String databaseUrl = getDatabase(username);
        
        return connections.get(databaseUrl);
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
    
    private static String getDatabase(String username) throws InvalidAuthor {
        char authorFirstLetter = Character.toLowerCase(username.charAt(0));
        
        String database;
        
        if (authorFirstLetter >= 'a' && authorFirstLetter <= 'j') {
            database = DATABASES[0];
        } else if (authorFirstLetter >= 'k' && authorFirstLetter <= 't') {
            database = DATABASES[1];
        } else if (authorFirstLetter >= 'u' && authorFirstLetter <= 'z') {
            database = DATABASES[2];
        } else {
            throw new InvalidAuthor("First letter of username must be a letter (from 'a' to 'z')");
        }
        
        return database;
    }
    
    @Nullable
    private Connection getConnectionByDbUrl(String databaseUrl) {
        String url = getConnectionUrl(databaseUrl);
        
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
            
            logger.info("Connected to " + url);
        } catch (SQLException e) {
            logger.warn("Error connecting to database " + url);
        }
        
        return connection;
    }
    
    @Override
    public long store(Message msg) throws SQLException, InvalidAuthor {
        String username = msg.getUsername();
        String text     = msg.getText();
        
        Connection connection = getConnectionByUsername(username);
        
        connection.setAutoCommit(false);
        
        String query = "INSERT INTO messages (user_name, text, ts) VALUES (?, ?, ?)";
        
        PreparedStatement statement = connection.prepareStatement(query);
        
        statement.setString(1, username);
        statement.setString(2, text);
        statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
        
        statement.execute();
        connection.commit();
        
        logger.info("Saved into database message: " + text + ", username: " + username);
        
        statement.close();
        
        logger.info("Saved message" + msg.getText() + " to database");
        
        return 0;
    }
    
    @Override
    public List<Message> getHistory(long from, long to, long limit) throws SQLException {
        List<Message> messages = new ArrayList<>();
        
        for (String database : DATABASES) {
            getMessage(database, messages, from, to, limit);
        }
    
        messages.sort((Message msg1, Message msg2) -> {
            long ts1 = msg1.getTimestamp();
            long ts2 = msg2.getTimestamp();
        
            return Long.compare(ts2, ts1);
        });
    
        List<Message> result = new ArrayList<>();
    
        for (int i = 0; i < result.size(); ++i) {
            result.add(messages.get(i));
        }
    
        Collections.reverse(result);
    
        return result;
    }
    
    private void getMessage(String database, List<Message> messages, long from, long to, long limit)
            throws SQLException {
        Connection connection = connections.get(database);
        
        connection.setAutoCommit(false);
    
        String query = "SELECT * FROM messages WHERE ts > ? AND ts < ? ORDER BY ts DESC LIMIT ?";
        
        PreparedStatement statement = connection.prepareStatement(query);
        
        statement.setTimestamp(1, new Timestamp(from));
        statement.setTimestamp(2, new Timestamp(to));
        statement.setLong(3, limit);
        
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Message message = new Message(resultSet.getString("user_name"), resultSet.getString("text"),
                                          resultSet.getTimestamp("ts").getTime()
            );
            
            messages.add(message);
        }
        
        connection.commit();
        
        statement.close();
    
    }
    
    @Override
    public List<Message> getByUser(String username, long limit) throws SQLException, InvalidAuthor {
        Connection connection = getConnectionByUsername(username);
    
        connection.setAutoCommit(false);
    
        String query = "SELECT * FROM messages WHERE user_name=? ORDER BY ts ASC LIMIT ?";
    
        PreparedStatement statement = connection.prepareStatement(query);
    
        statement.setString(1, username);
        statement.setLong(2, limit);
    
        List<Message> messages  = new ArrayList<>();
        ResultSet     resultSet = statement.executeQuery();
    
        while (resultSet.next()) {
            Message message = new Message(resultSet.getString("user_name"), resultSet.getString("text"),
                                          resultSet.getTimestamp("ts").getTime()
            );
        
            messages.add(message);
        }
    
        connection.commit();
    
        statement.close();
    
        return messages;
    }
}
