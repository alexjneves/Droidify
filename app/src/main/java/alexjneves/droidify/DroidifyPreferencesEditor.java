package alexjneves.droidify;

import android.content.SharedPreferences;

public final class DroidifyPreferencesEditor {
    private static final String LAST_PLAYED_TRACK_KEY = "LastPlayedTrack";
    private static final String LAST_PLAYED_TRACK_DEFAULT = "NonExistentTrack";

    private final SharedPreferences sharedPreferences;

    public DroidifyPreferencesEditor(final SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String readLastPlayedTrack() {
        return sharedPreferences.getString(LAST_PLAYED_TRACK_KEY, LAST_PLAYED_TRACK_DEFAULT);
    }

    public void writeLastPlayedTrack(final String lastPlayedTrack) {
        final SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        sharedPreferencesEditor.putString(LAST_PLAYED_TRACK_KEY, lastPlayedTrack);
        sharedPreferencesEditor.apply();
    }
}
