package org.example;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActivityRecords {
    public record Activity(
            String id,
            long timestamp,
            @SerializedName("$type") String type,
            Author author,
            Issue target,
            Field field,
            List<Value> added,
            List<Value> removed){
    }

    public record Author(String fullName){}

    public record Issue(String idReadable, String summary){}

    public record Field(String presentation){}

    public record Value(String name){}


}
