package ru.mephi.telegrambotdating_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelegramBotDatingJavaApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotDatingJavaApplication.class, args);
    }
}
