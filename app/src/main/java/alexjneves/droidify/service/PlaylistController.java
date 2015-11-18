package alexjneves.droidify.service;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class PlaylistController implements MediaPlayer.OnCompletionListener {
    private final Context applicationContext;
    private final Map<String, PlayableTrack> playableTrackMap;
    private final List<PlayableTrack> playableTracks;

    private List<PlayableTrack> playQueue;
    private List<MediaPlayer.OnCompletionListener> onCompletionListeners;
    private PlayableTrack currentTrack;
    private int currentTrackIndex;

    public PlaylistController(final List<String> resourcePaths, final Context applicationContext) {
        this.applicationContext = applicationContext;
        this.playableTrackMap = new HashMap<>();
        this.playableTracks = createPlayableTracks(resourcePaths);
        this.playQueue = new ArrayList<>(playableTracks);
        this.onCompletionListeners = new ArrayList<>();
        this.currentTrack = null;
        this.currentTrackIndex = 0;
    }

    public void changeTrack(final String resourcePath) {
        if (currentTrack != null) {
            currentTrack.stop();
            currentTrack.unregisterOnCompletionListener(this);
        }


        currentTrack = playableTrackMap.get(resourcePath);
        currentTrackIndex = playQueue.indexOf(currentTrack);

        currentTrack.registerOnCompletionListener(this);
    }

    public void playCurrentTrack() {
        currentTrack.play();
    }

    public void pauseCurrentTrack() {
        currentTrack.pause();
    }

    public PlayableTrack getCurrentTrack() {
        return currentTrack;
    }

    public PlayableTrack getNextTrack() {
        int nextTrackIndex = currentTrackIndex + 1;

        if (nextTrackIndex == playQueue.size()) {
            nextTrackIndex = 0;
        }

        return playQueue.get(nextTrackIndex);
    }

    public PlayableTrack getPreviousTrack() {
        int previousTrackIndex = currentTrackIndex - 1;

        if (previousTrackIndex < 0) {
            previousTrackIndex = playQueue.size() - 1;
        }

        return playQueue.get(previousTrackIndex);
    }

    public void setVolume(final float volume) {
        currentTrack.setVolume(volume);
    }

    public void shufflePlaylist() {
        Collections.shuffle(playQueue);
        currentTrackIndex = playQueue.indexOf(currentTrack);
    }

    public void resetShuffle() {
        playQueue = new ArrayList<>(playableTracks);
        currentTrackIndex = playQueue.indexOf(currentTrack);
    }

    public void registerOnCompletionListener(final MediaPlayer.OnCompletionListener onCompletionListener) {
        onCompletionListeners.add(onCompletionListener);
    }

    public void cleanUp() {
        // TODO: Do on list instead
        for (final Map.Entry<String, PlayableTrack> playableTrackEntry : playableTrackMap.entrySet()) {
            playableTrackEntry.getValue().stop();
        }
    }

    @Override
    public void onCompletion(final MediaPlayer mediaPlayer) {
        // TODO: Play next track

        for (final MediaPlayer.OnCompletionListener onCompletionListener : onCompletionListeners) {
            onCompletionListener.onCompletion(mediaPlayer);
        }
    }

    private List<PlayableTrack> createPlayableTracks(final List<String> resourcePaths) {
        final List<PlayableTrack> playableTracks = new ArrayList<>();

        for (final String resourcePath : resourcePaths) {
            final PlayableTrack playableTrack = new PlayableTrack(resourcePath, this.applicationContext);
            playableTrack.registerOnCompletionListener(this);

            playableTracks.add(playableTrack);
            playableTrackMap.put(resourcePath, playableTrack);
        }

        return playableTracks;
    }
}
