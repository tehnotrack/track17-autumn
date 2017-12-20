package ru.track.work_with_database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;



public class ConversationService {
    public ConversationService() {
    }

    /**
     * В зависимости от Message.senderName нужно сохранять в разные базы
     *
     * @return вернуть ID, который был присвоен сообщению в базе (поле ID)
     *
     *
     * 4 балла
     */



    private Connection conn = null;
    private PreparedStatement preparedStmt = null;
    private Properties connectionProps = null;

    private List<String> ports = Arrays.asList("tdb-1.trail5.net", "tdb-1.trail5.net", "tdb-1.trail5.net");
    //final static Logger logger = Logger.getLogger(ConversationService.class);
    long store(Message msg){
        ResultSet rs = null;
        long autoIncKeyFromApi = -1;
        setProperties();
        String port;
        if(msg.senderName.toLowerCase().charAt(0) >= 'a' && msg.senderName.toLowerCase().charAt(0) <= 'j') {
            port = ports.get(0);
        } else if(msg.senderName.toLowerCase().charAt(0) >= 'k' && msg.senderName.toLowerCase().charAt(0) <= 't'){
            port = ports.get(1);
        } else {
            port = ports.get(2);
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:" + "mysql" + "://" +
                            port +
                            ":" + "3306" + "/track17",
                    connectionProps);
            String query = " insert into messages (user_name, text, ts)" + " values (?, ?, ?)";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString (1, msg.senderName);
            preparedStmt.setString (2, msg.text);
            preparedStmt.setDate   (3, new Date(msg.timestamp));
            rs = preparedStmt.getGeneratedKeys();
            if (rs.next()) {
                autoIncKeyFromApi = rs.getLong(1);
            } else {

                // throw an exception from here
                throw new SQLException("can't get keys");
            }
            preparedStmt.execute();

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return autoIncKeyFromApi;
    }

    /**
     * Получить историю сообщений за период времени. Важно учесть лимит, чтобы не свалить базы слишком большой выборкой.
     *      Ограничить LIMIT нужно именно при запросе в базу
     * @param from - timestamp с какого времени
     * @param to - timestamp до какого времени
     * @param limit - максимальное кол-во ссобщений
     *
     * @return Список, отсротированный по timestamp
     *
     * 4 балла
     */
    List<Message> getHistory(long from, long to, long limit){
        List<Message> history = new ArrayList<>();
        setProperties();
        String query = "SELECT * FROM messages WHERE ts > ? AND ts < ? ORDER BY ts ASC LIMIT ?";
        long counter = 0;
        try {
            for(String port: ports){
                conn = DriverManager.getConnection(
                        "jdbc:" + "mysql" + "://" +
                                port +
                                ":" + "3306" + "/track17",
                        connectionProps);
                preparedStmt = conn.prepareStatement(query);
                preparedStmt.setTimestamp(1, new Timestamp(from));
                preparedStmt.setTimestamp(2, new Timestamp(to));
                preparedStmt.setLong(3, limit/3);
                ResultSet rs = preparedStmt.executeQuery();
                while (rs.next()){
                    Message message = new Message(rs.getString("user_name"), rs.getString("text"),
                            rs.getTimestamp("ts").getTime());
                    history.add(message);
                    counter++;
                }
                conn.commit();
                preparedStmt.close();
            }

        } catch (SQLException | NullPointerException ex){
            ex.printStackTrace();
        }
        return history;
    }


    /**
     * Вернуть все сообщения от определенного пользователя
     *
     * 4 балла
     */
    List<Message> getByUser(String username, long limit){
        List<Message> messages = new ArrayList<>();
        setProperties();
        String query = "SELECT * FROM messages WHERE user_name = ? LIMIT ?";
        int counter = 0;
        try {
            for(String port: ports) {
                conn = DriverManager.getConnection(
                        "jdbc:" + "mysql" + "://" +
                                port +
                                ":" + "3306" + "/track17",
                        connectionProps);
                preparedStmt = conn.prepareStatement(query);

                preparedStmt.setLong(3, limit/3);
                ResultSet rs = preparedStmt.executeQuery();
                while (rs.next()){
                    Message message = new Message(rs.getString("user_name"), rs.getString("text"),
                            rs.getTimestamp("ts").getTime());
                    messages.add(message);
                    counter++;
                }
                conn.commit();
                preparedStmt.close();
            }

        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return  messages;
    }

    private void setProperties(){
        connectionProps = new Properties();
        connectionProps.put("user", "track_student");
        connectionProps.put("password", "7EsH.H6x");
    }
}
