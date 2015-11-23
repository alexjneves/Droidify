package alexjneves.droidify;

import android.content.SharedPreferences;

final class DroidifyPreferencesEditor {
    private static final String LAST_PLAYED_TRACK_KEY = "LastPlayedTrack";
    private static final String LAST_PLAYED_TRACK_DEFAULT = "NonExistentTrack";

    private static final String SHUFFLE_ON_KEY = "ShuffleOn";
    private static final boolean SHUFFLE_ON_DEFAULT = false;

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

    public boolean readShuffleOn() {
        return sharedPreferences.getBoolean(SHUFFLE_ON_KEY, SHUFFLE_ON_DEFAULT);
    }

    public void writeShuffleOn(final boolean shuffleOn) {
        final SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        sharedPreferencesEditor.putBoolean(SHUFFLE_ON_KEY, shuffleOn);
        sharedPreferencesEditor.apply();
    }
}
