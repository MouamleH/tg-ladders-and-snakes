package me.mouamle.bot.game.objects.actions;

import lombok.Value;
import me.mouamle.bot.game.objects.Player;

@Value
public class BoardActionResult {

    /**
     * The reference to the player
     */
    Player player;

    /**
     * The player's position after the action
     */
    int playerPosition;

    /**
     * The response message
     */
    BoardMessage message;

}
