package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class YouTrackFetchService {
    private final String youtrackUrl;
    private final String youtrackToken;
    private final String targetChatId;
    private final MessengerBot bot;
    private final HttpClient httpClient;
    private final Gson gson;

    private long lastTimestamp;


    public YouTrackFetchService(String youtrackUrl, String youtrackToken, String targetChatId, MessengerBot bot){
        this.youtrackUrl = youtrackUrl;
        this.youtrackToken = "Bearer " + youtrackToken;
        this.targetChatId = targetChatId;
        this.bot = bot;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.lastTimestamp = System.currentTimeMillis();
    }

    public void startPolling(){
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::checkYouTrack, 0, 30, TimeUnit.SECONDS);
        System.out.println("YouTrack Poller started. Checking for updated every one 30 seconds");
    }

    private void checkYouTrack() {
        System.out.println("Checking for new activities!");
        try {
            String fields = "id,$type,timestamp,author(fullName),target(idReadable,summary)," +
                    "field(presentation),added(name),removed(name)";

            String url = String.format(
                    "%s/api/activities?categories=CustomFieldCategory,CommentsCategory,IssuesCategory" +
                            "&reverse=true&$top=15&start=%d&fields=%s",
                    this.youtrackUrl, this.lastTimestamp, fields
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", this.youtrackToken)
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                List<ActivityRecords.Activity> activities = gson.fromJson(response.body(),
                        new TypeToken<List<ActivityRecords.Activity>>() {}.getType());

                if (activities == null || activities.isEmpty()) {
                    return;
                }

                Collections.reverse(activities);

                for (ActivityRecords.Activity activity : activities) {
                    if (activity.timestamp() > this.lastTimestamp) {
                        String message = formatActivityMessage(activity);
                        if (message != null) {
                            bot.sendNotification(this.targetChatId, message);
                        }
                        this.lastTimestamp = activity.timestamp();
                    }
                }
            } else {
                System.err.println("Error fetching from YouTrack: " + response.statusCode() + " - Body: " + response.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatActivityMessage(ActivityRecords.Activity activity) {
        if (activity.target() == null || activity.author() == null) {
            return null;
        }

        String issueId = activity.target().idReadable();
        String summary = activity.target().summary();
        String author = activity.author().fullName();
        String activityType = activity.type();

        StringBuilder sb = new StringBuilder();

        if ("CommentActivityItem".equals(activityType)) {
            System.out.println("Comment Activity");
            sb.append(String.format("*New Comment by %s on issue `%s`*\n", author, issueId));
            sb.append(String.format("> %s", summary));

        } else if ("CustomFieldActivityItem".equals(activityType)) {
            System.out.println("Custom Field Activity");
            String fieldName = activity.field() != null ? activity.field().presentation() : "A field";
            sb.append(String.format("*%s updated issue `%s`*\n", author, issueId));
            sb.append(String.format("> %s\n\n", summary));

            String oldValue = activity.removed() != null && !activity.removed().isEmpty()
                    ? activity.removed().getFirst().name() : "_empty_";
            String newValue = activity.added() != null && !activity.added().isEmpty()
                    ? activity.added().getFirst().name() : "_empty_";

            sb.append(String.format("*Field:* %s\n", fieldName));
            sb.append(String.format("*From:* %s\n", oldValue));
            sb.append(String.format("*To:* %s", newValue));

        } else {
            System.out.println("Unknown Activity");
            return null;
        }

        return sb.toString();
    }

}
