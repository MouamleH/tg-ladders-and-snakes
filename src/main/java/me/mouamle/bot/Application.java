package me.mouamle.bot;

import lombok.extern.slf4j.Slf4j;
import me.mouamle.bot.bot.LaddersAndSnakesBot;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.ApiContextInitializer;

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(Application.class);
    }

    @Bean
    public ApplicationRunner init(LaddersAndSnakesBot bot) {
        return args -> {
//            log.info("Starting the bot...");
//            TelegramBotsApi api = new TelegramBotsApi();
//            api.registerBot(bot);
//            log.info("Bot started.");
        };
    }

}
