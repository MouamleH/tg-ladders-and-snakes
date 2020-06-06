package me.mouamle.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
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
