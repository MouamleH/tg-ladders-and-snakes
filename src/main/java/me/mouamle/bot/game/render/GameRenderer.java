package me.mouamle.bot.game.render;

import me.mouamle.bot.game.objects.GameSession;
import me.mouamle.bot.game.objects.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameRenderer {

    static Point[] pointsLookup = {
            new Point(1, 1),
            new Point(0, 0),
            new Point(2, 2),
            new Point(0, 2),
            new Point(2, 0),
            new Point(0, 1),
            new Point(2, 1)
    };

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
        Map<Integer, List<Player>> players = new HashMap<>();
        positions.forEach((player, position) -> {
            List<Player> list = players.getOrDefault(position, new ArrayList<>());
            list.add(player);
            players.put(position, list);
        });


        for (Map.Entry<Integer, List<Player>> entry : players.entrySet()) {
            Integer position = entry.getKey();
            List<PlayerPoint> points = generatePoints(entry.getValue());

            BufferedImage cellImage = new BufferedImage(140, 140, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = (Graphics2D) cellImage.getGraphics();

            int size = 28;

            for (PlayerPoint playerPoint : points) {
                g.setColor(playerPoint.player.getPlayerColor());
                Point point = playerPoint.point;

                g.fillOval(28 + point.x * size, 28 + point.y * size, size, size);

                g.setColor(Color.white);
                g.setStroke(new BasicStroke(2F));
                g.drawOval(28 + point.x * size, 28 + point.y * size, size, size);
            }

            int y = 10 - (position / 10) - 1;
            int x = y % 2 == 0 ? 9 - (position % 10) : position % 10;

            int px = 5 + (147 * x);
            int py = 5 + (147 * y);

            g.dispose();

            board.drawImage(cellImage, px, py, null);
        }
    }


    static List<PlayerPoint> generatePoints(List<Player> playersList) {
        List<PlayerPoint> points = new ArrayList<>();
        int players = playersList.size();

        if (players % 2 != 0) {
            points.add(new PlayerPoint(pointsLookup[0], playersList.get(0)));
        }

        if (points.size() == players) {
            return points;
        }

        for (int i = 1; points.size() != players; i++) {
            points.add(new PlayerPoint(pointsLookup[i], playersList.get(i - 1)));
        }

        return points;
    }

    private static class PlayerPoint {
        Point point;
        Player player;

        public PlayerPoint(Point point, Player player) {
            this.point = point;
            this.player = player;
        }
    }

}
