package me.mouamle.bot.game.objects;

import me.mouamle.bot.game.events.TickHandler;
import me.mouamle.bot.game.objects.actions.BoardActionResult;
import me.mouamle.bot.game.objects.actions.BoardMessage;

import java.util.*;

public class GameSession implements TickHandler {
    private final String sessionId = UUID.randomUUID().toString();

    private long chatId;
    private int creatorId;
    private int messageId;

    private GameState gameState = GameState.WAITING;

    private int currentPlayerIndex = 0;

    private List<Player> players = new LinkedList<>();
    private Map<Player, Integer> playersPositions = new HashMap<>();

    private Map<Player, Long> lastPlayerActions = new HashMap<>();

    public GameSession(long chatId, int creatorId, int messageId) {
        this.chatId = chatId;
        this.creatorId = creatorId;
        this.messageId = messageId;
    }

    @Override
    public void tick(long ticks) {

    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Player getNextPlayer() {
        int nextPlayerIndex = currentPlayerIndex + 1;
        if (nextPlayerIndex > players.size() - 1) {
            nextPlayerIndex = 0;
        }

        currentPlayerIndex = nextPlayerIndex;
        return players.get(currentPlayerIndex);
    }

    public boolean addPlayer(Player player) {
        if (gameState != GameState.WAITING) {
            return false;
        }

        players.add(player);
        playersPositions.put(player, 0);
        return true;
    }

    public boolean removePlayer(Player player) {
        if (gameState == GameState.FINISHED) {
            return false;
        }

        players.remove(player);
        playersPositions.put(player, 0);
        return true;
    }

    private BoardActionResult movePlayerTo(Player player, int position) {
        lastPlayerActions.put(player, System.currentTimeMillis());
        if (position > 100 || position < 0) {
            return new BoardActionResult(player, playersPositions.get(player), BoardMessage.OUT_OF_BOUND);
        }

        playersPositions.put(player, position);
        return new BoardActionResult(player, position, BoardMessage.OK);
    }

    private BoardActionResult movePlayerBy(Player player, int offset) {
        lastPlayerActions.put(player, System.currentTimeMillis());
        int currentPosition = playersPositions.getOrDefault(player, 0);
        if (currentPosition + offset > 100) {
            return new BoardActionResult(player, currentPosition, BoardMessage.MORE_THAN_100);
        }

        playersPositions.put(player, currentPosition + offset);
        return new BoardActionResult(player, currentPosition + offset, BoardMessage.OK);
    }

    public long getChatId() {
        return chatId;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public int getMessageId() {
        return messageId;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

}
