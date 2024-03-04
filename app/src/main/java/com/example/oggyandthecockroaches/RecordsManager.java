package com.example.oggyandthecockroaches;


import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecordsManager {

    private static final String PREF_KEY_PLAYER_RECORDS = "player_records";

    public static void addPlayerRecord(Context context, Player player) {
        List<Player> playerRecords = getStoredPlayerRecords(context);

        playerRecords.add(player);

        savePlayerRecords(context, playerRecords);
    }

    public static List<Player> getTopPlayers(Context context, int count) {
        List<Player> playerRecords = getStoredPlayerRecords(context);

        Collections.sort(playerRecords, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return Integer.compare(p2.getScore(), p1.getScore());
            }
        });

        if (count > playerRecords.size()) {
            count = playerRecords.size();
        }

        return playerRecords.subList(0, count);
    }
    private static List<Player> getStoredPlayerRecords(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("OggyAndTheCockroaches", Context.MODE_PRIVATE);
        String json = preferences.getString(PREF_KEY_PLAYER_RECORDS, "");

        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Player>>() {}.getType();

        return gson.fromJson(json, type);
    }

    private static void savePlayerRecords(Context context, List<Player> playerRecords) {
        SharedPreferences preferences = context.getSharedPreferences("OggyAndTheCockroaches", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(playerRecords);

        editor.putString(PREF_KEY_PLAYER_RECORDS, json);
        editor.apply();
    }
}