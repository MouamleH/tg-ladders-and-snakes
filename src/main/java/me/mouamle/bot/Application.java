package me.mouamle.bot;

import lombok.extern.slf4j.Slf4j;
import me.mouamle.bot.game.objects.GameSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootApplication
public class Application {

    static Point[] lookup = {
            new Point(1, 1),
            new Point(0, 0),
            new Point(2, 2),
            new Point(0, 2),
            new Point(2, 0),
            new Point(0, 1),
            new Point(2, 1)
    };

    static List<Point> generatePoint(int players) {
        List<Point> points = new ArrayList<>();
        if (players % 2 != 0) {
            points.add(lookup[0]);
        }

        if (points.size() == players) {
            return points;
        }

        for (int i = 1; points.size() != players; i++) {
            points.add(lookup[i]);
        }

        return points;
    }

    public static void main(String[] args) throws Exception {

//        BufferedImage image = new BufferedImage(140, 140, BufferedImage.TYPE_INT_ARGB);
//
//        int players = 6;
//
//        Graphics g = image.getGraphics();
//        {
//            Graphics2D g2d = (Graphics2D) g;
//
//            g2d.setColor(Color.black);
//            g2d.drawRect(0, 0, image.getWidth() - 1, image.getHeight() - 1);
//
//            g.setColor(Color.red.darker());
//            int size = 28;
//
//            List<Point> positions = generatePoint(players);
//            for (int i = 0; i < positions.size(); i++) {
//                g2d.setColor(GameSession.colors[i]);
//                Point position = positions.get(i);
//                g2d.fillOval(28 + position.x * size, 28 + position.y * size, size, size);
//
//                g2d.setColor(Color.white);
//                g2d.setStroke(new BasicStroke(2F));
//                g2d.drawOval(28 + position.x * size, 28 + position.y * size, size, size);
//            }
//        }
//        g.dispose();
//
//        File output = new File(String.format("%s.png", "cell"));
//        ImageIO.write(image, "PNG", output);
//        Desktop.getDesktop().open(output);
//        System.exit(0);

//
//        BufferedImage image = ImageIO.read(Resources.Board.EASY.getFile());
//
//        BufferedImage finalImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
//        Graphics g2 = finalImage.getGraphics();
//        {
//            g2.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
//            g2.setColor(Color.RED.brighter());
//
//            g2.setFont(new Font("Arial", Font.BOLD, 16));
//
//            for (int i = 0; i < 100; i++) {
//
//                int y = 10 - (i / 10) - 1;
//                int x = y % 2 == 0 ? 9 - (i % 10) : i % 10;
//
//                int px = 5 + (147 * x);
//                int py = 5 + (147 * y);
//
//                g2.drawRect(px, py, 140, 140);
//                g2.drawString("" + (i + 1), px + (140 / 2), py + (140 / 2));
//            }
//        }
//        g2.dispose();
//
//        File output = new File(String.format("%s.png", "Board"));
//        ImageIO.write(finalImage, "PNG", output);
//        Desktop.getDesktop().open(output);
//
//        System.exit(0);
        ApiContextInitializer.init();
        SpringApplication.run(Application.class);
    }

}
