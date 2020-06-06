package me.mouamle.bot.game;

import me.mouamle.bot.game.events.TickHandler;
import me.mouamle.bot.game.objects.GameSession;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class GameManager {

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final ConcurrentHashMap<Long, GameSession> sessions = new ConcurrentHashMap<>();
    private final AtomicLong ticksCounter = new AtomicLong();

    public GameManager() {
        executorService.scheduleWithFixedDelay(() -> {
            long tickCount = ticksCounter.getAndIncrement();
            if (ticksCounter.get() < 0) {
                ticksCounter.set(0);
            }

            for (TickHandler tickHandler : sessions.values()) {
                tickHandler.tick(tickCount);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public GameManagerResponse newGame(long chatId, int userId, int messageId) {
        if (sessions.containsKey(chatId)) {
            return new GameManagerResponse("A Chat can't have multiple running games", false);
        }

        GameSession session = new GameSession(chatId, userId, messageId);
        sessions.put(chatId, session);

        return new GameManagerResponse("ok", true);
    }

    public GameManagerResponse diceRolled(long chatId, int userId, int value) {
        return null;
    }

    public GameManagerResponse playerJoinRequest(long chatId, int userId, int messageId) {
        if (!sessions.containsKey(chatId)) {
            return new GameManagerResponse("expired game", false);
        }
        GameSession session = sessions.get(chatId);


        return new GameManagerResponse("ok", true);
    }

    public GameManagerResponse gameStartRequest(long chatId, int userId, int messageId) {
        if (!sessions.containsKey(chatId)) {
            return new GameManagerResponse("expired game", false);
        }
        GameSession session = sessions.get(chatId);

        return new GameManagerResponse("ok", true);
    }

    public GameManagerResponse gameCancelRequest(long chatId, int userId, int messageId) {
        if (!sessions.containsKey(chatId)) {
            return new GameManagerResponse("expired game", false);
        }
        GameSession session = sessions.get(chatId);

        return new GameManagerResponse("ok", true);
    }

}
