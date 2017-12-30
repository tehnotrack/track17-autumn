package ru.track.prefork;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class MessageStorage {
    private Connection[] database;
    private PreparedStatement[] preparedInsert;
    private PreparedStatement[] preparedGetHist;
    private PreparedStatement[] preparedGetByUser;

    public MessageStorage() throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException{
        database = new Connection[3];
        int i;
        DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());
        StringBuilder url;
        for(i = 0; i < 3; i++) {
            url = new StringBuilder();
            url.
                    append("jdbc:mysql://").        //db type
                    append(String.format("tdb-%d.trail5.net:", i + 1)).            //host name
                    append("3306/").                //port
                    append("track17?").            //db name
                    append("user=track_student&").            //login
                    append("password=7EsH.H6x");        //password
            database[i] = DriverManager.getConnection(url.toString());
        }

        String insert = "insert into messages(user_name, text, ts) values (?, ?, ?)";
        preparedInsert = new PreparedStatement[3];
        for(i = 0; i < 3; i++)
            preparedInsert[i] = database[i].prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);

        String getHist = "select * from messages where ts between ? and ? order by ts limit ?";
        preparedGetHist = new PreparedStatement[3];
        for(i = 0; i < 3; i++)
            preparedGetHist[i] = database[i].prepareStatement(getHist);

        String getByUser = "select * from messages where user_name = ? order by ts limit ?";
        preparedGetByUser = new PreparedStatement[3];
        for(i = 0; i < 3; i++)
            preparedGetByUser[i] = database[i].prepareStatement(getByUser);
    }

    public long store(Message msg) throws SQLException{
        String senderName = msg.getSenderName();
        char c = Character.toLowerCase(senderName.charAt(0));
        int i;
        if(c < 'k')
            i = 0;
        else if(c < 'u')
            i = 1;
        else
            i = 2;
        preparedInsert[i].setString(1, senderName);
        preparedInsert[i].setString(2, msg.getData());
        preparedInsert[i].setDate(3, new Date(msg.getTs()));
        preparedInsert[i].execute();
        ResultSet generatedKeys = preparedInsert[i].getGeneratedKeys();
        generatedKeys.next();
        return generatedKeys.getLong(1);
    }

    List<Message> getHistory(long from, long to, long limit) throws SQLException{
        int i;
        for(i = 0; i < 3; i++){
            preparedGetHist[i].setDate(1, new Date(from));
            preparedGetHist[i].setDate(2, new Date(to));
            preparedGetHist[i].setLong(3, limit);
        }
        ResultSet[] rs = new ResultSet[3];
        for(i = 0; i < 3; i++)
            rs[i] = preparedGetHist[i].executeQuery();
        List<Message> history = new LinkedList<>();
        Message[] msg = new Message[3];
        for(i = 0; i < 3; i++) {
            if (rs[i].next())
                msg[i] = new Message(rs[i].getDate("ts").getTime(), rs[i].getString("text"), rs[i].getString("user_name"));
            else msg[i] = null;
        }
        while(history.size() < limit){
            if(msg[0] == null && msg[1] == null && msg[2] == null)
                break;
            for(i = 0; i < 3; i++) {
                if (msg[i] != null && (msg[(i + 1) % 3] == null || msg[i].getTs() <= msg[(i + 1) % 3].getTs()) && (msg[(i + 2) % 3] == null || msg[i].getTs() <= msg[(i + 2) % 3].getTs())) {
                    history.add(msg[i]);
                    if (rs[i].next())
                        msg[i] = new Message(rs[i].getDate("ts").getTime(), rs[i].getString("text"), rs[i].getString("user_name"));
                    else msg[i] = null;
                }
            }
        }
        return history;
    }

    List<Message> getByUser(String username, long limit) throws SQLException{
        char c = Character.toLowerCase(username.charAt(0));
        int i;
        if(c < 'k')
            i = 0;
        else if(c < 'u')
            i = 1;
        else
            i = 2;
        preparedGetByUser[i].setString(1, username);
        preparedGetByUser[i].setLong(2, limit);
        ResultSet rs = preparedGetByUser[i].executeQuery();
        List<Message> history = new LinkedList<>();
        while(rs.next())
            history.add(new Message(rs.getDate("ts").getTime(), rs.getString("text"), rs.getString("user_name")));
        return history;
    }
}
