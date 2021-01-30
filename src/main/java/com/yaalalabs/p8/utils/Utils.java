package com.yaalalabs.p8.utils;

import io.vertx.core.json.JsonObject;

import java.time.Instant;

public class Utils {

    public static long getCurrentEpoch() {
        return Instant.now().toEpochMilli();
    }

    public static void updateTimeTrack(JsonObject obj, String trackId){
        JsonObject track = obj.getJsonObject("track");
        track.put(trackId, getCurrentEpoch());
    }
}
