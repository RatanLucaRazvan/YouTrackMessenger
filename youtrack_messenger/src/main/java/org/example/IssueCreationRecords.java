package org.example;

public class IssueCreationRecords {
    public record Project(String id) {
    }

    public record IssuePayload(String summary, Project project){}

    public record IssueResponse(String idReadable, String summary) {}
}
