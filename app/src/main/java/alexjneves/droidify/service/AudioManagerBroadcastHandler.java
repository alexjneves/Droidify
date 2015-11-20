package alexjneves.droidify.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * Responsible for receiving the system broadcast indicating that the audio output hardware has
 * changed.
 */
public final class AudioManagerBroadcastHandler extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            return;
        }

        final Intent pausePlayback = new Intent(context, DroidifyPlayerService.class);
        pausePlayback.setAction(DroidifyPlayerService.PAUSE_PLAYBACK_INTENT_ACTION);

        context.startService(pausePlayback);
    }
}
