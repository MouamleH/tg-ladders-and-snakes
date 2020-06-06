package me.mouamle.bot.game.objects;

import lombok.extern.slf4j.Slf4j;
import me.mouamle.bot.game.Resources;
import me.mouamle.bot.game.events.TickHandler;
import me.mouamle.bot.game.objects.actions.BoardActionResult;
import me.mouamle.bot.game.objects.actions.BoardMessage;

import java.awt.*;
import java.util.List;
import java.util.*;

@Slf4j
public class GameSession implements TickHandler {

    private final String sessionId = UUID.randomUUID().toString();
    private static final Color[] colors = {
            new Color(223, 57, 67),  // Red
            new Color(72, 178, 43),  // Green
            new Color(33, 76, 198),  // Blue
            new Color(149, 13, 237), // Purple
            new Color(249, 221, 0),  // Yellow
            new Color(249, 126, 14)  // Orange
    };

    private long chatId;
    private int creatorId;
    private int messageId;

    private GameState gameState = GameState.WAITING;
    private Resources.Board gameBoard;

    private int currentPlayerIndex = 0;
    private List<Player> players = new LinkedList<>();

    private Map<Player, Integer> playersPositions = new HashMap<>();
    private Map<Player, Long> lastPlayerActions = new HashMap<>();

    public GameSession(long chatId, int creatorId, int messageId, Resources.Board gameBoard) {
        this.chatId = chatId;
        this.creatorId = creatorId;
        this.messageId = messageId;
        this.gameBoard = gameBoard;
    }

    @Override
    public void tick(long ticks) {

    }

    public BoardActionResult startSession(Player player) {
        if (player.getUserId() != creatorId) {
            return new BoardActionResult(player, 0, BoardMessage.NOT_YOUR_GAME);
        }

        setGameState(GameState.STARTED);
        return new BoardActionResult(player, 0, BoardMessage.OK);
    }

    public BoardActionResult diceRoll(Player player, int value) {
        log.info("Player {} rolled {}", player.getUsername(), value);
        if (player.getUserId() != getCurrentPlayer().getUserId()) {
            return new BoardActionResult(player, playersPositions.get(player), BoardMessage.NOT_YOUR_TURN);
        }

        BoardActionResult boardActionResult = movePlayerBy(player, value);
        if (boardActionResult.getMessage() != BoardMessage.OK) {
            return boardActionResult;
        }

        getNextPlayer();
        return new BoardActionResult(player, playersPositions.get(player), BoardMessage.OK);
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
        log.info("Player {} is joining", player.getUsername());
        if (gameState != GameState.WAITING) {
            return false;
        }

        player.setPlayerColor(colors[players.size()]);
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
        log.info("Moving player {} by {}", player.getUsername(), offset);
        int currentPosition = playersPositions.getOrDefault(player, 0);
        if (currentPosition + offset > 100) {
            return new BoardActionResult(player, currentPosition, BoardMessage.MORE_THAN_100);
        }

        playersPositions.put(player, currentPosition + offset);
        return new BoardActionResult(player, currentPosition + offset, BoardMessage.OK);
    }

    public int getPlayerPosition(int userId) {
        Optional<Player> oPlayer = players.stream().filter(player -> player.getUserId() == userId).findFirst();
        if (!oPlayer.isPresent()) {
            return 0;
        }

        Player player = oPlayer.get();
        return playersPositions.get(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Map<Player, Integer> getPlayersPositions() {
        return playersPositions;
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

    public Resources.Board getGameBoard() {
        return gameBoard;
    }
}
