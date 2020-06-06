package me.mouamle.bot.bot.keyboard;

import mouamle.generator.KeyboardGenerator;
import mouamle.generator.classes.ButtonHolder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardUtils {

    public static List<List<InlineKeyboardButton>> generateKeyboard(Object data) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        try {
            List<List<ButtonHolder>> buttons = KeyboardGenerator.getInstance().generateKeyboard(data);
            for (List<ButtonHolder> button : buttons) {
                List<InlineKeyboardButton> row = new ArrayList<>();
                for (ButtonHolder holder : button) {
                    row.add(new InlineKeyboardButton(holder.getText()).setCallbackData(holder.getData()));
                }
                keyboard.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return keyboard;
    }

}
