

import java.sql.*;
import java.util.*;

/**
 *
 */
public class ConversationService {

    protected Map<Integer, Connection> listOfConnections;

    ConversationService(){
        listOfConnections = new HashMap<>();
        setUpConnection();
    }

    private void setUpConnection() {
        String dataBaseName = null;
        int counter=0;
        for ( counter = 0; counter < 3; counter++){
            dataBaseName = "tdb-"+Integer.toString(counter+1)+".trail5.net:";
            try {
                DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());
                StringBuilder url = new StringBuilder();
                url.
                        append("jdbc:mysql://").        //db type
                        append(dataBaseName).           //host name
                        append("3306/").                //port
                        append("track17?").             //db name
                        append("user=track_student&").  //login
                        append("password=7EsH.H6x");    //password
                listOfConnections.put( counter, DriverManager.getConnection(url.toString()));
            } catch ( SQLException e ) {
                System.out.println("Something went wrong during work with data base!");
                e.printStackTrace();
            } catch (  IllegalAccessException   e){
                System.out.println("Something went wrong during attempt to access data base!");
                e.printStackTrace();
            } catch (  ClassNotFoundException    e){
                System.out.println("Something went wrong! Declared class wasn't found!");
                e.printStackTrace();
            } catch (  InstantiationException    e){
                System.out.println("Something went wrong!");
                e.printStackTrace();
            }
        }
    }

    private Connection findShardInDataBase(String userName) throws IllegalArgumentException{
        char firstLetterOfUsername = userName.charAt(0);
        try {
            if ( ( ('a' <= firstLetterOfUsername) && (firstLetterOfUsername <= 'j') ) || ( ('A' <= firstLetterOfUsername) && (firstLetterOfUsername <= 'J') ) ){
                return listOfConnections.get(0);
            }else if ((('k' <= firstLetterOfUsername) && (firstLetterOfUsername <= 't')) || (('K' <= firstLetterOfUsername) && (firstLetterOfUsername <= 'T'))){
                return listOfConnections.get(1);
            }else if ((('u' <= firstLetterOfUsername) && (firstLetterOfUsername <= 'z')) || (('U' <= firstLetterOfUsername) && (firstLetterOfUsername <= 'Z'))){
                return listOfConnections.get(2);
            }else {
                throw new IllegalArgumentException();
            }
        }
        catch (IllegalArgumentException e) {
            System.out.println("Wrong userName - it should start with english character!");
            e.printStackTrace();
        }
    }

    private List<Message> createListOfMessages(ResultSet myResultSet) throws SQLException{
        List<Message> myList = new ArrayList<>();
        try{
            while (myResultSet.next()) {
                Message myMessage = new Message();
                myMessage.ownerLogin = myResultSet.getString("user_name");
                myMessage.text = myResultSet.getString("text");
                myMessage.ts = myResultSet.getLong("ts");
                myList.add(myMessage);
            }
            return myList;
        } catch (Exception e) {
            System.out.println("Something went wrong!");
            e.printStackTrace();
        }
    }

    public long store(Message msg) {
    Connection myConnection = findShardInDataBase(msg.ownerLogin);
    try ( PreparedStatement myStatement = myConnection.prepareStatement
            ("INSERT INTO messages(user_name, text, ts) VALUE (?, ?, now())",
                    Statement.RETURN_GENERATED_KEYS)) {

        myStatement.setString(1, msg.ownerLogin);
        myStatement.setString(2, msg.text);
        myStatement.execute();
        try( ResultSet generatedKeys = myStatement.getGeneratedKeys();){
            generatedKeys.next();
            return generatedKeys.getLong(1);
        } catch(Exception e){
            System.out.println("Something went wrong!");
           }
        } catch (SQLException e) {
            System.out.println("Something went wrong during work with data base!");
            e.printStackTrace();
        }
    }

    List<Message> getHistory(long from, long to, long limit){
        List<Message> listOfMessage = new ArrayList<>();
        for(int counter = 0; counter < 3; counter++){
            try( PreparedStatement myStatement = listOfConnections.get(counter).prepareStatement
                    ("SELECT * FROM messages WHERE ts BETWEEN ? AND now() ORDER BY ts ASC LIMIT ?",
                            Statement.RETURN_GENERATED_KEYS);) {
                myStatement.setTimestamp(1, new Timestamp(from - 1));
                myStatement.setLong(2, limit);
                try( ResultSet myResultSet = myStatement.executeQuery();){
                    listOfMessage.addAll(createListOfMessages(myResultSet));
                    listOfMessage.sort(new Comparator<Message>() {
                        @Override
                        public int compare(Message message1, Message message2) {
                            return Long.compare(message1.ts , message2.ts)  ;
                        }
                    });
                    int res=0;
                    if (limit < listOfMessage.size()){
                        res=(int) limit;
                    } else {
                        res= listOfMessage.size();
                    }
                    return listOfMessage.subList(0, res);
                } catch (Exception e) {
                    System.out.println("Something went wrong!");
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                System.out.println("Something went wrong during work with data base!");
                e.printStackTrace();
            }
        }
    }

    List<Message> getByUser(String username, long limit){
        Connection myConnection = findShardInDataBase(username);
        try(  PreparedStatement myStatement = myConnection.prepareStatement
                ("SELECT * FROM messages WHERE user_name=? LIMIT ?",
                        Statement.RETURN_GENERATED_KEYS)){
            myStatement.setString(1, username);
            myStatement.setLong(2, limit);
            try( ResultSet myResultSet = myStatement.executeQuery()){
                return createListOfMessages(myResultSet);
            } catch (Exception e) {
                System.out.println("Something went wrong!");
                e.printStackTrace();
            }
        }catch (SQLException e){
            System.out.println("Something went wrong during work with data base!");
            e.printStackTrace();
        }
        return null;
    }

}