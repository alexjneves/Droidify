package alexjneves.droidify;

import android.view.View;
import android.widget.Button;

import alexjneves.droidify.service.IDroidifyPlayer;

public final class TrackShuffleButton implements View.OnClickListener{
    private final Button shuffleButton;
    private final IDroidifyPlayer droidifyPlayer;
    private boolean shuffleOn;

    public static TrackShuffleButton create(final Button shuffleButton, final IDroidifyPlayer droidifyPlayer, final boolean activated) {
        final TrackShuffleButton trackShuffleButton = new TrackShuffleButton(shuffleButton, droidifyPlayer, activated);
        shuffleButton.setOnClickListener(trackShuffleButton);

        return trackShuffleButton;
    }

    private TrackShuffleButton(final Button shuffleButton, final IDroidifyPlayer droidifyPlayer, final boolean activated) {
        this.shuffleButton = shuffleButton;
        this.droidifyPlayer = droidifyPlayer;
        this.shuffleOn = activated;

        toggleShuffle();
    }

    @Override
    public void onClick(final View view) {
        shuffleOn = !shuffleOn;
        toggleShuffle();
    }

    private void toggleShuffle() {
        droidifyPlayer.toggleShuffle(shuffleOn);

        if (shuffleOn) {
            shuffleButton.setBackgroundResource(R.drawable.shuffle_button_on);
        } else {
            shuffleButton.setBackgroundResource(R.drawable.shuffle_button_off);
        }
    }
}
