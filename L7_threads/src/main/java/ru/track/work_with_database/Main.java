package ru.track.work_with_database;

public class Main {
    public static void main(String[] args){
        ConversationService conversationService = new ConversationService();
        conversationService.store(new Message("jhgfd", "hugyfd", 3445));
    }
}
