package me.mouamle.bot.game.objects.actions;

public enum BoardMessage {

    MORE_THAN_100("Dice result was more than 100, Can't move"),
    OUT_OF_BOUND("Position out of bound"),
    OK("Valid move"),
    NOT_YOUR_TURN("Not your turn"),
    NOT_YOUR_GAME("Can't start a you haven't created");

    private final String message;

    BoardMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
