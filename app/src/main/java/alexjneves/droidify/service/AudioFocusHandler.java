package alexjneves.droidify.service;

import android.content.Context;
import android.media.AudioManager;

/**
 * Responsible for handling audio focus, responding appropriately to system events which indicate
 * the audio focus state has changed. Provides methods for requesting and releasing audio focus.
 */
public final class AudioFocusHandler implements AudioManager.OnAudioFocusChangeListener {
    private static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private static final float MAX_VOLUME = 1.0f;
    private static final float MIN_VOLUME = 1.0f;

    private final IDroidifyPlayer droidifyPlayer;
    private final AudioManager audioManager;

    private int previousAudioFocusState;

    public AudioFocusHandler(final IDroidifyPlayer droidifyPlayer, final Context context) {
        this.droidifyPlayer = droidifyPlayer;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.previousAudioFocusState = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    public boolean requestAudioFocus() {
        final int audioFocusRequestResult = audioManager.requestAudioFocus(this, STREAM_TYPE, AudioManager.AUDIOFOCUS_GAIN);

        return audioFocusRequestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocus() {
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(final int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                droidifyPlayer.setVolume(MAX_VOLUME);

                if (previousAudioFocusState != AudioManager.AUDIOFOCUS_LOSS) {
                    droidifyPlayer.playCurrentTrack();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                droidifyPlayer.pauseCurrentTrack();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                droidifyPlayer.setVolume(MIN_VOLUME);
                break;
            default:
                break;
        }

        previousAudioFocusState = focusChange;
    }
}
