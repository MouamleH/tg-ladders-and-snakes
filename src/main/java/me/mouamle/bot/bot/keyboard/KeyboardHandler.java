package me.mouamle.bot.bot.keyboard;

import me.mouamle.bot.bot.LaddersAndSnakesBot;
import me.mouamle.bot.data.UserBaseService;
import me.mouamle.bot.game.GameManager;
import me.mouamle.bot.game.GameManagerResponse;
import me.mouamle.bot.game.objects.GameSession;
import me.mouamle.bot.game.objects.Player;
import mouamle.generator.annotation.KeyboardCallback;
import mouamle.generator.annotation.callbacks.ValueCallback;
import mouamle.processor.classess.Callback;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@KeyboardCallback(name = "main-handler")
public class KeyboardHandler {

    private final LaddersAndSnakesBot bot;
    private final GameManager gameManager;
    private final UserBaseService userBaseService;

    public KeyboardHandler(LaddersAndSnakesBot bot, UserBaseService userBaseService) {
        this.bot = bot;
        this.gameManager = bot.getGameManager();
        this.userBaseService = userBaseService;
    }

    @ValueCallback(valueKey = GameStartKeyboard.KEY)
    public void onGameMessagePressed(Callback callback) {
        CallbackQuery query = callback.getCallbackQuery();

        final User from = query.getFrom();

        int userId = from.getId();
        long chatId = query.getMessage().getChatId();
        int messageId = query.getMessage().getMessageId();

        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(query.getId());
        answer.setCacheTime(0);
        answer.setText("Ok");

        Player player = new Player();
        player.setUserId(userId);
        player.setUsername(from.getUserName());
        player.setDisplayName(String.valueOf(from.getFirstName()));
        userBaseService.savePlayer(player);

        String data = callback.getValue();
        if (data.equals(GameStartKeyboard.TEXT_START_GAME)) {
            GameManagerResponse response = gameManager.gameStartRequest(chatId, userId, messageId);
            if (!response.isSuccessful()) {
                answer.setShowAlert(true);
                answer.setText(response.getMessage());
            } else {
                EditMessageReplyMarkup edit = new EditMessageReplyMarkup();
                edit.setChatId(String.valueOf(chatId));
                edit.setMessageId(query.getMessage().getMessageId());
                edit.setReplyMarkup(new InlineKeyboardMarkup());
                bot.execute(edit);

                SendDice sendDice = new SendDice();
                sendDice.setChatId(chatId);
                sendDice.setEmoji("\uD83C\uDFB2");
                bot.execute(sendDice);
            }
        } else if (data.equals(GameStartKeyboard.TEXT_JOIN_GAME)) {
            GameManagerResponse response = gameManager.playerJoinRequest(chatId, userId, messageId);
            if (!response.isSuccessful()) {
                answer.setShowAlert(true);
                answer.setText(response.getMessage());
            } else {
                GameSession session = gameManager.getSession(chatId);

                StringBuilder playersList = new StringBuilder("Players List:\n");
                for (Player sessionPlayer : session.getPlayers()) {
                    playersList.append("- ").append(sessionPlayer.getDisplayName()).append('\n');
                }

                EditMessageCaption edit = new EditMessageCaption();
                edit.setChatId(String.valueOf(chatId));
                edit.setMessageId(query.getMessage().getMessageId());
                edit.setCaption(playersList.toString());
                edit.setReplyMarkup(query.getMessage().getReplyMarkup());
                bot.execute(edit);
            }
        } else if (data.equals(GameStartKeyboard.TEXT_CANCEL_GAME)) {
            GameManagerResponse response = gameManager.gameCancelRequest(chatId, userId, messageId);
            if (!response.isSuccessful()) {
                answer.setShowAlert(true);
                answer.setText(response.getMessage());
            } else {
                EditMessageReplyMarkup edit = new EditMessageReplyMarkup();
                edit.setChatId(String.valueOf(chatId));
                edit.setMessageId(query.getMessage().getMessageId());
                edit.setReplyMarkup(new InlineKeyboardMarkup());
                bot.execute(edit);
            }
        } else {
            answer.setShowAlert(true);
            answer.setText("Invalid Operation, try again in 5 seconds.");
        }

        bot.execute(answer);
    }


}
