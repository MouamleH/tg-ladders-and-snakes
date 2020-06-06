package me.mouamle.bot.game.render;

import me.mouamle.bot.game.objects.GameSession;
import me.mouamle.bot.game.objects.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class GameRenderer {

    public static File renderGame(GameSession session) {
        try {
            BufferedImage image = ImageIO.read(session.getGameBoard().getFile());

            BufferedImage finalImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = finalImage.getGraphics();
            {
                g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
                renderPlayers(g, session.getPlayersPositions());
            }
            g.dispose();

            File output = new File(String.format("%d.png", session.getChatId()));
            ImageIO.write(finalImage, "PNG", output);
            return output;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void renderPlayers(Graphics board, Map<Player, Integer> positions) {
        positions.forEach((player, position) -> {
            BufferedImage image = new BufferedImage(140, 140, BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();
            g.setColor(player.getPlayerColor());
            g.fillOval(10, 10, 100, 100);

            int y = 10 - (position / 10) - 1;
            int x = y % 2 == 0 ? 9 - (position % 10) : position % 10;

            int px = 5 + (147 * x);
            int py = 5 + (147 * y);

            g.dispose();

            board.drawImage(image, px, py, null);
        });
    }

}
