package me.mouamle.bot.game;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class Resources {

    public File readBoardFile() {
        return new File("boards/easy.png");
    }

    public static enum Board {
        EASY("boards/easy.png"),
        NORMAL("boards/normal.png"),
        HARD("boards/hard.png"),
        DEADLY("boards/deadly.png");

        private final File file;

        Board(String path) {
            this.file = new File(path);
        }

        public File getFile() {
            return file;
        }
    }

}
