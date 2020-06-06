package me.mouamle.bot.game.objects;

import me.mouamle.bot.game.events.TickHandler;
import me.mouamle.bot.game.objects.actions.BoardActionResult;
import me.mouamle.bot.game.objects.actions.BoardMessage;

import java.util.*;

public class GameSession implements TickHandler {

    private long chatId;
    private final String sessionId = UUID.randomUUID().toString();

    private int currentPlayerIndex = 0;

    private List<Player> players = new LinkedList<>();
    private Map<Player, Integer> playersPositions = new HashMap<>();

    private Map<Player, Long> lastPlayerActions = new HashMap<>();

    public GameSession(long chatId) {
        this.chatId = chatId;
    }

    public Player getNextPlayer() {
        int nextPlayerIndex = currentPlayerIndex + 1;
        if (nextPlayerIndex > players.size() - 1) {
            nextPlayerIndex = 0;
        }

        currentPlayerIndex = nextPlayerIndex;
        return players.get(currentPlayerIndex);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void addPlayer(Player player) {
        players.add(player);
        playersPositions.put(player, 0);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        playersPositions.put(player, 0);
    }

    private BoardActionResult movePlayerTo(Player player, int position) {
        if (position > 100 || position < 0) {
            return new BoardActionResult(player, playersPositions.get(player), BoardMessage.OUT_OF_BOUND);
        }

        playersPositions.put(player, position);
        return new BoardActionResult(player, position, BoardMessage.OK);
    }

    private BoardActionResult movePlayerBy(Player player, int offset) {
        int currentPosition = playersPositions.getOrDefault(player, 0);
        if (currentPosition + offset > 100) {
            return new BoardActionResult(player, currentPosition, BoardMessage.MORE_THAN_100);
        }

        playersPositions.put(player, currentPosition + offset);
        return new BoardActionResult(player, currentPosition + offset, BoardMessage.OK);
    }

    @Override
    public void tick() {

    }

    public long getChatId() {
        return chatId;
    }
}
