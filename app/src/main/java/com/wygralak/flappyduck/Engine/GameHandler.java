package com.wygralak.flappyduck.Engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/**
 * Created by robertogiba on 20.10.2017.
 */

public class GameHandler {
    private static final String PREFS_GAME = "GamePrefs";
    private static final String PREF_GAME_HIGHSCORE = "gameHighScorePref";

    public static int loadHighScore(Context context) {
        final SharedPreferences preferences = context.getSharedPreferences(PREFS_GAME, Context.MODE_PRIVATE);
        return preferences.getInt(PREF_GAME_HIGHSCORE, 0);
    }

    public static void saveGameStats(int score, Context context) {
        final SharedPreferences preferences = context.getSharedPreferences(PREFS_GAME, Context.MODE_PRIVATE);
        int highScore = preferences.getInt(PREF_GAME_HIGHSCORE, 0);

        if (score > highScore) {
            highScore = score;

            final SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(PREF_GAME_HIGHSCORE, highScore);
            editor.apply();
            editor.commit();
        }
    }
}
