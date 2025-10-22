package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessengerBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String YOUTRACK_PROJECT_ID;
    private final YouTrackCreationService youTrackService;

    public MessengerBot(String botToken, String botUsername, String youtrackProjectId, YouTrackCreationService service){
        super(botToken);
        this.botUsername = botUsername;
        this.YOUTRACK_PROJECT_ID = youtrackProjectId;
        this.youTrackService = service;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if(messageText.equals("/start")){
                sendNotification(chatId, "Bot is active and listening");
            } else if (messageText.startsWith("/create")){
                handleCreateCommand(chatId, messageText);
            } else {
                sendNotification(chatId, "Hello! To create an issue, use the command:\n`/create [summary of the issue]`");
            }
        }
    }

    public void sendNotification(String chatId, String messageText){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageText);
        message.enableMarkdown(true);

        try{
            execute(message);
            System.out.println("Successfully sent message to chat ID " + chatId);
        } catch(TelegramApiException e){
            System.err.println("Failed to send message to chat ID " + chatId);
            e.printStackTrace();
        }
    }

    private void handleCreateCommand(String chatId, String messageText){
        String summary = messageText.substring(8).trim();
        sendNotification(chatId, "Attempting to create issue...");

        try{
            IssueCreationRecords.IssueResponse newIssue = youTrackService.createIssue(YOUTRACK_PROJECT_ID, summary);
            String successMessage = String.format("*Success!* Issue `%s` created.\n> %s", newIssue.idReadable(), newIssue.summary());
            sendNotification(chatId, successMessage);
        } catch (Exception e){
            e.printStackTrace();
            sendNotification(chatId, "*Error!* Could not create issue. Please check the console logs.");
        }
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }
}
