package me.mouamle.bot.game;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class Resources {

    public File readBoardFile() {
        return new File("boards/easy.JPG");
    }

}
