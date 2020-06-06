package me.mouamle.bot.game;

import org.springframework.stereotype.Component;

@Component
public class GameManager {

    public void newGame(long chatId, int userId, int messageId) {

    }

    public GameManagerResponse playerJoinRequest(long chatId, int userId, int messageId) {
        return new GameManagerResponse("ok", true);
    }

    public GameManagerResponse gameStartRequest(long chatId, int userId, int messageId) {
        return new GameManagerResponse("ok", true);
    }

    public GameManagerResponse gameCancelRequest(long chatId, int userId, int messageId) {
        return new GameManagerResponse("ok", true);
    }

}
