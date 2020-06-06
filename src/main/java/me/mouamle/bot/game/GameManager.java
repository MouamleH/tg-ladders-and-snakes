package me.mouamle.bot.game;

import lombok.extern.slf4j.Slf4j;
import me.mouamle.bot.data.UserBaseService;
import me.mouamle.bot.game.events.TickHandler;
import me.mouamle.bot.game.objects.GameSession;
import me.mouamle.bot.game.objects.Player;
import me.mouamle.bot.game.objects.actions.BoardActionResult;
import me.mouamle.bot.game.objects.actions.BoardMessage;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class GameManager {

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final ConcurrentHashMap<Long, GameSession> sessions = new ConcurrentHashMap<>();
    private final AtomicLong ticksCounter = new AtomicLong();

    private final UserBaseService userBaseService;

    public GameManager(UserBaseService userBaseService) {
        this.userBaseService = userBaseService;
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
        log.info("Player {} starting a game in chat {}", userId, chatId);
        if (sessions.containsKey(chatId)) {
            log.warn("Chat {} already has a running game", chatId);
            return new GameManagerResponse("A Chat can't have multiple running games", false);
        }

        GameSession session = new GameSession(chatId, userId, messageId, Resources.Board.EASY);
        sessions.put(chatId, session);

        return new GameManagerResponse("ok", true);
    }

    public GameManagerResponse diceRolled(long chatId, int userId, int value) {
        log.info("Dice rolled in chat {} by {}", chatId, userId);
        if (!sessions.containsKey(chatId)) {
            return new GameManagerResponse("Expired game!", false);
        }
        GameSession session = sessions.get(chatId);

        Optional<Player> oPlayer = userBaseService.getPlayerByUserId(userId);
        if (!oPlayer.isPresent()) {
            return new GameManagerResponse("An error occurred", false);
        }

        Player player = oPlayer.get();

        BoardActionResult result = session.diceRoll(player, value);
        if (result.getMessage() != BoardMessage.OK) {
            return new GameManagerResponse(result.getMessage().getMessage(), false);
        }

        return new GameManagerResponse("ok", true);
    }

    public GameManagerResponse playerJoinRequest(long chatId, int userId, int messageId) {
        log.info("Player {} requested to join a game in chat {}", userId, chatId);
        if (!sessions.containsKey(chatId)) {
            return new GameManagerResponse("Expired game!", false);
        }
        GameSession session = sessions.get(chatId);

        Optional<Player> oPlayer = userBaseService.getPlayerByUserId(userId);
        if (!oPlayer.isPresent()) {
            return new GameManagerResponse("An error occurred", false);
        }

        Player player = oPlayer.get();

        boolean added = session.addPlayer(player);
        if (!added) {
            return new GameManagerResponse("Can't join this game", false);
        }

        return new GameManagerResponse("ok", true);
    }

    public GameManagerResponse gameStartRequest(long chatId, int userId, int messageId) {
        log.info("Player {} started the game in chat {}", userId, chatId);
        if (!sessions.containsKey(chatId)) {
            return new GameManagerResponse("expired game", false);
        }
        GameSession session = sessions.get(chatId);

        if (session.getCreatorId() != userId) {
            return new GameManagerResponse("Can't start a game that is not yours", false);
        }

        Optional<Player> oPlayer = userBaseService.getPlayerByUserId(userId);
        if (!oPlayer.isPresent()) {
            return new GameManagerResponse("An error occurred", false);
        }

        Player player = oPlayer.get();

        BoardActionResult result = session.startSession(player);

        if (result.getMessage() != BoardMessage.OK) {
            return new GameManagerResponse(result.getMessage().getMessage(), false);
        }

        return new GameManagerResponse("ok", true);
    }

    public GameManagerResponse gameCancelRequest(long chatId, int userId, int messageId) {
        if (!sessions.containsKey(chatId)) {
            return new GameManagerResponse("expired game", false);
        }

        GameSession session = sessions.get(chatId);
        if (session.getCreatorId() != userId) {
            return new GameManagerResponse("Can't cancel a game that is not yours", false);
        }

        sessions.remove(chatId);

        return new GameManagerResponse("ok", true);
    }

    public GameSession getSession(long chatId) {
        return sessions.get(chatId);
    }

}
