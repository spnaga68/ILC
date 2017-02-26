package realmstudy.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 *this class is used to store the values locally and used it whenever necessary
 */

public class SessionSave {
public static final String CURRENTLY_MATCH_GOING="current_match";
    public static void saveSession(String key, String value, Context context) {
        if (context != null) {
            Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
            editor.putString(key, value);
            editor.commit();
        }
    }
    public static void saveSession(String key, int value, Context context) {
        if (context != null) {
            Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
            editor.putInt(key, value);
            editor.commit();
        }
    }
    public static Integer getSessionInt(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
        return prefs.getInt(key, 0);
    }
    public static String getSession(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public static void saveSession(String key, boolean value, Context context) {
        Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBooleanSession(String key, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    public static void clearSession(Context context) {
        Editor editor = context.getSharedPreferences("KEY", Activity.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    public static void tutorialChk(boolean isAsk, Context con) {
        Editor editor = con.getSharedPreferences("ASK", con.MODE_PRIVATE).edit();
        editor.putBoolean("isAsk", isAsk);
        editor.commit();
    }

    public static boolean isAsk(Context con) {
        SharedPreferences sharedPreferences = con.getSharedPreferences("ASK", con.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isAsk", false);

    }
}
