package me.mouamle.bot.bot.keyboard;

import me.mouamle.bot.bot.LaddersAndSnakesBot;
import me.mouamle.bot.game.GameManager;
import me.mouamle.bot.game.GameManagerResponse;
import mouamle.generator.annotation.KeyboardCallback;
import mouamle.generator.annotation.callbacks.ValueCallback;
import mouamle.processor.classess.Callback;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@KeyboardCallback(name = "main-handler")
public class KeyboardHandler {

    private final LaddersAndSnakesBot bot;
    private final GameManager gameManager;

    public KeyboardHandler(LaddersAndSnakesBot bot) {
        this.bot = bot;
        this.gameManager = bot.getGameManager();
    }

    @ValueCallback(valueKey = GameStartKeyboard.KEY)
    public void onGameMessagePressed(Callback callback) {
        CallbackQuery query = callback.getCallbackQuery();

        int userId = query.getFrom().getId();
        long chatId = query.getMessage().getChatId();
        int messageId = query.getMessage().getMessageId();

        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(query.getId());
        answer.setCacheTime(5);

        String data = query.getData();
        if (data.equals(GameStartKeyboard.TEXT_START_GAME)) {
            GameManagerResponse response = gameManager.gameStartRequest(chatId, userId, messageId);
            if (!response.isSuccessful()) {
                answer.setShowAlert(true);
                answer.setText(response.getMessage());
            }

        } else if (data.equals(GameStartKeyboard.TEXT_JOIN_GAME)) {
            GameManagerResponse response = gameManager.playerJoinRequest(chatId, userId, messageId);
            if (!response.isSuccessful()) {
                answer.setShowAlert(true);
                answer.setText(response.getMessage());
            }

        } else if (data.equals(GameStartKeyboard.TEXT_CANCEL_GAME)) {
            GameManagerResponse response = gameManager.gameCancelRequest(chatId, userId, messageId);
            if (!response.isSuccessful()) {
                answer.setShowAlert(true);
                answer.setText(response.getMessage());
            }


        } else {
            answer.setShowAlert(true);
            answer.setText("Invalid Operation, try again in 5 seconds.");
        }

        bot.execute(answer);
    }


}
