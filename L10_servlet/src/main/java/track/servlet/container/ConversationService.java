package track.servlet.container;

import java.sql.*;
import java.util.*;

/**
 *
 */
public class ConversationService {

    protected Map<Integer, Connection> connectionsMap;

    ConversationService(){
        connectionsMap = new HashMap<>();
        connect();
    }

    private void connect() {
        String baseName = null;
        for (int i = 0; i < 3; i++){
            baseName = "tdb-"+ i +".trail5.net:";
            try {
                DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());
                StringBuilder url = new StringBuilder();
                url.
                        append("jdbc:mysql://").        //db type
                        append(baseName).               //host name
                        append("3306/").                //port
                        append("track17?").             //db name
                        append("user=track_student&").  //login
                        append("password=7EsH.H6x");    //password
                connectionsMap.put(i, DriverManager.getConnection(url.toString()));
            } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Connection returnDbconnection(String username) throws IllegalArgumentException{
        char firstLetter = username.charAt(0);
        String baseName = null;
        if (('a' <= firstLetter) && (firstLetter <= 'j')){
            return connectionsMap.get(0);
        }else if (('k' <= firstLetter) && (firstLetter <= 't')){
            return connectionsMap.get(1);
        }else if (('u' <= firstLetter) && (firstLetter <= 'z')){
            return connectionsMap.get(2);
        }else {
            throw new IllegalArgumentException();
        }
    }

    public long store(Message msg) {
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            Connection connection = returnDbconnection(msg.ownerLogin);
            statement = connection.prepareStatement
                    ("INSERT INTO messages(user_name, text, ts) VALUE (?, ?, now())",
                            Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, msg.ownerLogin);
            statement.setString(2, msg.text);
//            statement.setTimestamp(3, new Timestamp( msg.ts));

            statement.execute();

            generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            return generatedKeys.getLong(1);


        }catch (SQLException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            System.out.println("Username must starts with english character!");
            e.printStackTrace();
        }finally{
            try {
                if (statement != null)
                    statement.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
            try{
                if (generatedKeys != null)
                generatedKeys.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    List<Message> getHistory(long from, long to, long limit){

        try {
            PreparedStatement[] statements = new PreparedStatement[3];
            List<Message> msgList = new ArrayList<>();
            ResultSet resultSet = null;
            for(int i = 0; i < 3; i++){
                try {
                    statements[i] = connectionsMap.get(i).prepareStatement
                            ("SELECT * FROM messages WHERE ts BETWEEN ? AND now() ORDER BY ts ASC LIMIT ?",
                                    Statement.RETURN_GENERATED_KEYS);
                    statements[i].setTimestamp(1, new Timestamp(from - 1));
//                statements[i].setTimestamp(2, new Timestamp(from + 1));
                    statements[i].setLong(2, limit);

                    resultSet = statements[i].executeQuery();
                    msgList.addAll(toMsgList(resultSet));
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if (statements[i] != null)
                            statements[i].close();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    try{
                        if (resultSet != null)
                        resultSet.close();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            msgList.sort(new Comparator<Message>() {
                @Override
                public int compare(Message o1, Message o2) {
                    return o1.ts > o2.ts ? 1 : o1.ts == o2.ts ? 0 : -1;
                }
            });
            return msgList.subList(0, limit < msgList.size() ? (int) limit : msgList.size());
        }catch (IllegalArgumentException e){
            System.out.println("Username must starts with english character!");
            e.printStackTrace();
        }
        return null;
    }

    private List<Message> toMsgList(ResultSet resultSet) throws SQLException{
        List<Message> list = new ArrayList<>();
        while (resultSet.next()) {
            Message msg = new Message();
            msg.ownerLogin = resultSet.getString("user_name");
            msg.text = resultSet.getString("text");
            msg.ts = resultSet.getLong("ts");

            list.add(msg);
        }
        return list;
    }

    List<Message> getByUser(String username, long limit){
    PreparedStatement statement = null;
    ResultSet resultSet = null;
        try{
            Connection connection = returnDbconnection(username);
            statement = connection.prepareStatement
                    ("SELECT * FROM messages WHERE user_name=? LIMIT ?",
                            Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, username);
            statement.setLong(2, limit);

            resultSet = statement.executeQuery();

            return toMsgList(resultSet);

        }catch (IllegalArgumentException e){
            System.out.println("Username must starts with english character!");
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                if (statement != null)
                statement.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return null;
    }

}
