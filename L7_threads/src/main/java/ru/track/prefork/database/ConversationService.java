package ru.track.prefork.database;

import ru.track.prefork.Message;

import java.util.List;

public interface ConversationService {
    /**
     * В зависимости от Message.senderName нужно сохранять в разные базы
     *
     * @return вернуть ID, который был присвоен сообщению в базе (поле ID)
     * <p>
     * <p>
     * 4 балла
     */
    long store(Message msg);

    /**
     * Получить историю сообщений за период времени. Важно учесть лимит, чтобы не свалить базы слишком большой выборкой.
     * Ограничить LIMIT нужно именно при запросе в базу
     *
     * @param from  - timestamp с какого времени
     * @param to    - timestamp до какого времени
     * @param limit - максимальное кол-во ссобщений
     * @return Список, отсротированный по timestamp
     * <p>
     * 4 балла
     */
    List<Message> getHistory(long from, long to, long limit);


    /**
     * Вернуть все сообщения от определенного пользователя
     * <p>
     * 4 балла
     */
    List<Message> getByUser(String username, long limit);
}
