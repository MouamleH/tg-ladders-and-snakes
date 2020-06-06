package me.mouamle.bot.bot;

import lombok.extern.slf4j.Slf4j;
import me.mouamle.bot.bot.keyboard.GameStartKeyboard;
import me.mouamle.bot.bot.keyboard.KeyboardHandler;
import me.mouamle.bot.bot.keyboard.KeyboardUtils;
import me.mouamle.bot.data.UserBaseService;
import me.mouamle.bot.game.GameManager;
import me.mouamle.bot.game.GameManagerResponse;
import me.mouamle.bot.game.Resources;
import me.mouamle.bot.game.objects.GameSession;
import me.mouamle.bot.game.render.GameRenderer;
import mouamle.processor.KeyboardProcessor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Dice;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

@Slf4j
@Component
public class LaddersAndSnakesBot extends TelegramLongPollingBot {

    private final Resources resources;
    private final GameManager gameManager;

    public LaddersAndSnakesBot(Resources resources, GameManager gameManager, UserBaseService userBaseService) {
        this.resources = resources;
        this.gameManager = gameManager;
        KeyboardProcessor.registerHandler(new KeyboardHandler(this, userBaseService));
    }

    public void onUpdateReceived(Update update) {
        log.info("New update {}", update);

        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.isGroupMessage() || message.isSuperGroupMessage()) {
                // message.getChatId() != -350997585
//                if (message.getChatId() != -1001278135261L) {
//                    execute(new SendMessage(message.getChatId(), "Can't use me, yet"));
//                    return;
//                }
                handleGroupMessage(message);
            } else if (message.isUserMessage()) {
                handleUserMessage(message);
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            try {
                boolean processed = KeyboardProcessor.processCallback(callbackQuery);
                if (!processed) {
                    handleCallbackQuery(callbackQuery);
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }


    private void handleUserMessage(Message message) {
        if (message.hasText()) {
            if (message.getText().startsWith("/start")) {
                SendMessage method = new SendMessage()
                        .setChatId(message.getChatId())
                        .setText("Welcome to *Ladders & Snakes*.\n\n" +
                                "- Add me to a group\n" +
                                "- Make me an admin :)\n" +
                                "- Send `/game` in the group\n\n" +
                                "*Things to keep in mind*\n" +
                                "- A group *can't have multiple games* running at the same time\n" +
                                "- I need to be an admin to *delete the dice message*\n" +
                                "- I might throttle down because of the load.\n\n" +
                                "One more thing, Send me a dice and i will tell you the number before you see it!.")
                        .enableMarkdown(true);
                execute(method);
            }
        } else if (message.hasDice()) {
            Dice dice = message.getDice();
            if (dice.getEmoji().equals("\uD83C\uDFB2")) {
                SendMessage method = new SendMessage()
                        .setChatId(message.getChatId())
                        .setText("With the powers of magic i knew your number will be " + dice.getValue())
                        .enableMarkdown(true);
                execute(method);
            }
        }
    }

    private void handleGroupMessage(Message message) {
        if (message.hasText()) {
            String text = message.getText();
            if (text.equals("/game")) {
                // Start a new game session with the chat id
                SendPhoto send = new SendPhoto();
                send.setCaption("New Game Started!");
                send.setChatId(message.getChatId());
                send.setPhoto(resources.readBoardFile());
                send.setReplyMarkup(new InlineKeyboardMarkup(KeyboardUtils.generateKeyboard(new GameStartKeyboard())));

                try {
                    Message sentMessage = execute(send);
                    long chatId = message.getChatId();
                    int userId = message.getFrom().getId();

                    GameManagerResponse response = gameManager.newGame(chatId, userId, sentMessage.getMessageId());
                    if (!response.isSuccessful()) {
                        execute(new DeleteMessage(sentMessage.getChatId(), sentMessage.getMessageId()));
                        execute(new SendMessage(message.getChatId(), response.getMessage()));
                    }
                } catch (TelegramApiException e) {
                    execute(new SendMessage(message.getChatId(), "Could not start a game!"));
                }
            }
        } else if (message.hasDice()) {
            // Handle a player dice roll
            Dice dice = message.getDice();
            if (dice.getEmoji().equals("\uD83C\uDFB2")) {
                Integer value = dice.getValue();
                GameManagerResponse response = gameManager.diceRolled(message.getChatId(), message.getFrom().getId(), value);
                if (!response.isSuccessful()) {
                    execute(new SendMessage(message.getChatId(), response.getMessage()));
                    return;
                }

                Message statusMessage = execute(new SendMessage(message.getChatId(), "moving by: " + value).setReplyToMessageId(message.getMessageId()));

                GameSession session = gameManager.getSession(message.getChatId());
                File file = GameRenderer.renderGame(session);

                StringBuilder caption = new StringBuilder("Last Movement\n");
                caption.append(message.getFrom().getFirstName())
                        .append(" moved to: ").append(session.getPlayerPosition(message.getFrom().getId()) + 1).append("\n\n");

                caption.append("Players List:\n");
                session.getPlayersPositions().forEach((player, position) -> {
                    caption.append("- ").append(player.getDisplayName()).append(" is at: ").append(position + 1).append('\n');
                });

                InputMediaPhoto media = new InputMediaPhoto();
                media.setMedia(file, "test");
                media.setCaption(caption.toString());

                EditMessageMedia edit = new EditMessageMedia();
                edit.setChatId(session.getChatId());
                edit.setMessageId(session.getMessageId());
                edit.setMedia(media);

                try {
                    execute(edit);
                    execute(new DeleteMessage(message.getChatId(), message.getMessageId()));
                    execute(new DeleteMessage(message.getChatId(), statusMessage.getMessageId()));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {

    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
        try {
            return super.execute(method);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public String getBotUsername() {
        return "LaddersAndSnakesBot";
    }

    public String getBotToken() {
        return "1188215806:AAEwbVG0cJz6GgTsAhIpbXP8fPGzI1iOjaY";
    }

}
