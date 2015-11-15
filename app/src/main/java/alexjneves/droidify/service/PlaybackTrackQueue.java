package alexjneves.droidify.service;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class PlaybackTrackQueue implements MediaPlayer.OnCompletionListener {
    private final Context applicationContext;
    private final Map<String, PlayableTrack> queuedTracks;

    private PlayableTrack currentTrack;
    private List<MediaPlayer.OnCompletionListener> onCompletionListeners;

    public PlaybackTrackQueue(final List<String> resourcePaths, final Context applicationContext) {
        this.applicationContext = applicationContext;
        this.queuedTracks = new HashMap<>();
        currentTrack = null;
        onCompletionListeners = new ArrayList<>();

        final List<PlayableTrack> playableTracks = createPlayableTracks(resourcePaths);
        //linkTracks(playableTracks);
    }

    public void changeTrack(final String resourcePath) {
        if (currentTrack != null) {
            currentTrack.stop();
        }

        currentTrack = queuedTracks.get(resourcePath);
    }

    public void playCurrentTrack() {
        currentTrack.registerOnCompletionListener(this);

        currentTrack.play();
    }

    public void pauseCurrentTrack() {
        currentTrack.pause();
    }

    public void skipForward() {

    }

    public void skipBackward() {

    }

    public void setVolume(final float volume) {
        currentTrack.setVolume(volume);
    }

    public void registerOnCompletionListener(final MediaPlayer.OnCompletionListener onCompletionListener) {
        onCompletionListeners.add(onCompletionListener);
    }

    public void cleanUp() {
        for (final Map.Entry<String, PlayableTrack> playableTrackEntry : queuedTracks.entrySet()) {
            playableTrackEntry.getValue().stop();
        }
    }

    private List<PlayableTrack> createPlayableTracks(final List<String> resourcePaths) {
        final List<PlayableTrack> playableTracks = new ArrayList<>();

        for (final String resourcePath : resourcePaths) {
            final PlayableTrack playableTrack = PlayableTrack.Create(resourcePath, this.applicationContext);
            playableTrack.registerOnCompletionListener(this);

            playableTracks.add(playableTrack);
            queuedTracks.put(resourcePath, playableTrack);
        }

        return playableTracks;
    }

//    private void linkTracks(final List<PlayableTrack> playableTracks) {
//        if (playableTracks.isEmpty()) {
//            return;
//        }
//
//        final MediaPlayer firstMediaPlayer = playableTracks.get(0).getMediaPlayer();
//        MediaPlayer previousMediaPlayer = firstMediaPlayer;
//
//        for (int i = 1; i < playableTracks.size(); ++i) {
//            final MediaPlayer nextMediaPlayer = playableTracks.get(i).getMediaPlayer();
//
//            previousMediaPlayer.setNextMediaPlayer(nextMediaPlayer);
//            previousMediaPlayer = nextMediaPlayer;
//        }
//
//        previousMediaPlayer.setNextMediaPlayer(firstMediaPlayer);
//    }

    @Override
    public void onCompletion(final MediaPlayer mediaPlayer) {
        // TODO: Play next track

        for (final MediaPlayer.OnCompletionListener onCompletionListener : onCompletionListeners) {
            onCompletionListener.onCompletion(mediaPlayer);
        }
    }
}
