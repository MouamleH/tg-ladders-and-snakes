package me.mouamle.bot.bot.keyboard;

import mouamle.generator.annotation.handlers.value.ButtonGroupValue;
import mouamle.generator.annotation.handlers.value.ButtonValue;

public final class GameStartKeyboard {

    public static final String KEY = "msg-game";

    public static final String TEXT_JOIN_GAME = "join-game";
    public static final String TEXT_START_GAME = "start-game";
    public static final String TEXT_CANCEL_GAME = "cancel-game";

    @ButtonValue(text = "Join Game", key = KEY, callbackText = TEXT_JOIN_GAME)
    private Object joinGame;

    @ButtonGroupValue(key = KEY, texts = {"Start", "Cancel"}, callbacks = {TEXT_START_GAME, TEXT_CANCEL_GAME})
    private Object gameControl;

}
