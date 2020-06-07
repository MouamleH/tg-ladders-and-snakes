package me.mouamle.bot.game;

import lombok.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class Resources {

    public File readBoardFile() {
        return new File("boards/easy.png");
    }

    @Value
    public static class Ladder {
        int start;
        int end;
    }

    @Value
    public static class Snecc {
        int start;
        int end;
    }

    public enum Board {
        EASY("boards/easy.png",
                new Snecc[]{
                        new Snecc(15, 5),
                        new Snecc(47, 31),
                        new Snecc(89, 53),
                        new Snecc(96, 39),
                },
                new Ladder[]{
                        new Ladder(2, 18),
                        new Ladder(10, 28),
                        new Ladder(25, 44),
                        new Ladder(54, 76),
                        new Ladder(60, 62),
                        new Ladder(69, 90),
                        new Ladder(82, 98),
                }),

        NORMAL("boards/normal.png", new Snecc[]{}, new Ladder[]{}),
        HARD("boards/hard.png", new Snecc[]{}, new Ladder[]{}),
        DEADLY("boards/deadly.png", new Snecc[]{}, new Ladder[]{});

        private final File file;

        private final Snecc sneccs[];
        private final Ladder ladders[];

        Board(String path, Snecc[] sneccs, Ladder[] ladders) {
            this.file = new File(path);
            this.sneccs = sneccs;
            this.ladders = ladders;
        }

        public File getFile() {
            return file;
        }

        public Snecc[] getSneccs() {
            return sneccs;
        }

        public Ladder[] getLadders() {
            return ladders;
        }

    }

}
