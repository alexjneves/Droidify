package alexjneves.droidify;

import android.view.View;
import android.widget.Button;

import alexjneves.droidify.service.DroidifyPlayerState;
import alexjneves.droidify.service.IDroidifyPlayer;
import alexjneves.droidify.service.IDroidifyPlayerStateChangeListener;

public final class TrackPlayPauseButton implements IDroidifyPlayerStateChangeListener, View.OnClickListener {
    private final IDroidifyPlayer droidifyPlayer;
    private final Button uiButton;
    private final IRunOnUiThread runOnUiThread;

    private DroidifyPlayerState currentState;
    private boolean awaitingStateUpdate;

    static TrackPlayPauseButton Create(final IDroidifyPlayer droidifyPlayer, final Button uiButton, final IRunOnUiThread runOnUiThread) {
        final TrackPlayPauseButton trackPlayPauseButton = new TrackPlayPauseButton(droidifyPlayer, uiButton, runOnUiThread);

        droidifyPlayer.registerStateChangeListener(trackPlayPauseButton);
        uiButton.setOnClickListener(trackPlayPauseButton);

        return trackPlayPauseButton;
    }

    private TrackPlayPauseButton(final IDroidifyPlayer droidifyPlayer, final Button uiButton, final IRunOnUiThread runOnUiThread) {
        this.droidifyPlayer = droidifyPlayer;
        this.uiButton = uiButton;
        this.runOnUiThread = runOnUiThread;

        currentState = DroidifyPlayerState.PAUSED;
        awaitingStateUpdate = false;
    }

    @Override
    public void onDroidifyPlayerStateChange(final DroidifyPlayerState newState) {
        currentState = newState;
        awaitingStateUpdate = false;

        switch (newState) {
            case PAUSED:
                updateButtonUi(R.drawable.play_button);
                break;
            case PLAYING:
                updateButtonUi(R.drawable.pause_button);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(final View view) {
        if (awaitingStateUpdate) {
            return;
        }

        awaitingStateUpdate = true;

        switch (currentState) {
            case PAUSED:
                droidifyPlayer.playCurrentTrack();
                break;
            case PLAYING:
                droidifyPlayer.pauseCurrentTrack();
                break;
            default:
                break;
        }
    }

    private void updateButtonUi(final int resourceId) {
        final Runnable updateButtonUi = new Runnable() {
            @Override
            public void run() {
                uiButton.setBackgroundResource(resourceId);
            }
        };

        runOnUiThread.run(updateButtonUi);
    }
}
