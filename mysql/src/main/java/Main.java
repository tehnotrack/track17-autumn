import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args){
        ConversationService conversationService = new ConversationService();
//        try {
//            java.util.Date today = new java.util.Date();
//            java.sql.Timestamp ts1 = new java.sql.Timestamp(today.getTime());
//            java.sql.Timestamp ts2 = java.sql.Timestamp.valueOf("2005-04-06 09:01:10");
//            java.sql.Timestamp ts3 = java.sql.Timestamp.valueOf("2005-04-06 12:01:10");
//            java.sql.Timestamp ts4 = java.sql.Timestamp.valueOf("2005-04-06 18:01:10");
//            java.sql.Timestamp ts5 = java.sql.Timestamp.valueOf("2005-04-07 09:01:10");
//
//            long tsTime1 = ts1.getTime();
//            long tsTime2 = ts2.getTime();
//            long tsTime3 = ts3.getTime();
//            long tsTime4 = ts4.getTime();
//            long tsTime5 = ts5.getTime();
//            long key1 = conversationService.store(new Message("jhgfd", "hugyfd",tsTime1));
//            long key2 = conversationService.store(new Message("jhglk", "jhgfyg", tsTime2));
//            long key3 = conversationService.store(new Message("rhylk", "jhgfyg", tsTime3));
//            long key4 = conversationService.store(new Message("rhelk", "jhgfyg", tsTime4));
//            long key5 = conversationService.store(new Message("whalk", "jhgfyg", tsTime5));
//            System.out.println(key1);
//            System.out.println(key2);
//            System.out.println(key3);
//            System.out.println(key4);
//            System.out.println(key5);
//            List<Message> list = conversationService.getHistory(java.sql.Timestamp.valueOf("2005-04-05 09:01:10").getTime(), java.sql.Timestamp.valueOf("2005-04-08 09:01:10").getTime(), 20 );
//            for(Message msg: list){
//                System.out.println(msg.senderName + " " + msg.text);
//            }

//
        List<Message> messages = conversationService.getByUser("jhglk", 5);
        for(Message msg: messages){
            System.out.println(msg.senderName + " " + msg.text);
        }
    }
}
