package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class YouTrackCreationService {
    public record ProjectInfo(String id, String name, String shortName) {}
    private final String youtrackUrl;
    private final String youtrackToken;
    private final HttpClient httpClient;
    private final Gson gson;

    public YouTrackCreationService(String youtrackUrl, String youtrackToken) {
        this.youtrackUrl = youtrackUrl;
        this.youtrackToken = "Bearer " + youtrackToken;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }
    public IssueCreationRecords.IssueResponse createIssue(String projectId, String summary) throws Exception{
        IssueCreationRecords.Project project = new IssueCreationRecords.Project(projectId);
        IssueCreationRecords.IssuePayload payload = new IssueCreationRecords.IssuePayload(summary, project);
        String jsonPayload = gson.toJson(payload);
        String fields = "idReadable,summary";
        String url = String.format("%s/api/issues/?fields=%s", youtrackUrl, fields);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .header("Authorization", this.youtrackToken)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200){
            return gson.fromJson(response.body(), IssueCreationRecords.IssueResponse.class);
        } else {
            throw new RuntimeException("Failed to create issue. Status: " + response.statusCode() + " Body: " + response.body());
        }
    }

    public void listProjectsAndFindId() throws Exception {
        System.out.println("--- Finding Project IDs ---");
        String fields = "id,name,shortName";
        String url = String.format("%s/api/admin/projects?fields=%s", youtrackUrl, fields);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .header("Authorization", this.youtrackToken)
                .header("Accept", "application/json").build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Found the following projects:");
            // Parse the JSON array into a List of ProjectInfo objects
            List<ProjectInfo> projects = gson.fromJson(response.body(), new TypeToken<List<ProjectInfo>>() {}.getType());
            // Loop through and print a formatted table
            for (ProjectInfo project : projects) {
                System.out.format("  - Project Name: %-20s | shortName: %-10s | INTERNAL ID: %s\n",
                        project.name(), project.shortName(), project.id());
            }
            System.out.println("\nFind your project in the list above and use its INTERNAL ID in your Main.java configuration.");
        } else {
            System.err.println("Error fetching projects: " + response.statusCode() + " - " + response.body());
        }
        System.out.println("--- Test Complete ---");
    }
}
