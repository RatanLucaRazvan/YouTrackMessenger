package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        final String YOUTRACK_URL = System.getenv("YOUTRACK_URL");
        final String YOUTRACK_TOKEN = System.getenv("YOUTRACK_TOKEN");
        final String YOUTRACK_PROJECT_ID = System.getenv("YOUTRACK_PROJECT_ID");
        final String BOT_TOKEN = System.getenv("BOT_TOKEN");
        final String BOT_USERNAME = System.getenv("BOT_USERNAME");
        final String TARGET_CHAT_ID = System.getenv("TARGET_CHAT_ID");

        if (YOUTRACK_URL == null || YOUTRACK_TOKEN == null || YOUTRACK_PROJECT_ID == null ||
                BOT_TOKEN == null || BOT_USERNAME == null || TARGET_CHAT_ID == null) {
            System.err.println("Error: One or more required environment variables are not set.");
            System.err.println("Please set YOUTRACK_URL, YOUTRACK_TOKEN, YOUTRACK_PROJECT_ID, " +
                    "BOT_TOKEN, BOT_USERNAME, and TARGET_CHAT_ID.");
            return;
        }

        try{
            YouTrackCreationService creationService = new YouTrackCreationService(YOUTRACK_URL, YOUTRACK_TOKEN);
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            MessengerBot bot = new MessengerBot(BOT_TOKEN, BOT_USERNAME, YOUTRACK_PROJECT_ID, creationService);
            botsApi.registerBot(bot);
            System.out.println("Bot is registered and listening for commands");

            YouTrackFetchService service = new YouTrackFetchService(YOUTRACK_URL, YOUTRACK_TOKEN, TARGET_CHAT_ID, bot);
            service.startPolling();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}