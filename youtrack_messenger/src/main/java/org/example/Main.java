package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        final String YOUTRACK_URL = "https://lucaratan.youtrack.cloud";
        final String YOUTRACK_TOKEN = "perm-YWRtaW4=.NDQtMA==.YFWfoXaGs1LaEPTTBouzi4Gw1xKGYZ";
        final String YOUTRACK_PROJECT_ID = "0-0";
        final String BOT_TOKEN = "8324600435:AAGONo0JhTJA1ZP3GKC0PrOzhmTbkt5XsXs";
        final String BOT_USERNAME = "lucaratan_bot";
        final String TARGET_CHAT_ID = "8308599765";

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