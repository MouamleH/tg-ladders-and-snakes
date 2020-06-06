package me.mouamle.bot.game.events;

public interface TickHandler {

    /**
     * Called every second
     *
     * @param ticks represents the passed ticks
     */
    void tick(long ticks);

}
