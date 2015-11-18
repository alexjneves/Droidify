package alexjneves.droidify.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class PlaylistController implements ITrackCompleteListener {
    private final Context applicationContext;
    private final ITrackChangedListener trackChangedListener;
    private final Map<String, PlayableTrack> playableTrackMap;
    private final List<PlayableTrack> playableTracks;

    private List<PlayableTrack> playQueue;
    private List<ITrackCompleteListener> trackCompleteListeners;
    private PlayableTrack currentTrack;
    private int currentTrackIndex;

    public PlaylistController(final List<String> resourcePaths, final Context applicationContext, final ITrackChangedListener trackChangedListener) {
        this.applicationContext = applicationContext;
        this.trackChangedListener = trackChangedListener;
        this.playableTrackMap = new HashMap<>();
        this.playableTracks = createPlayableTracks(resourcePaths);

        this.playQueue = new ArrayList<>(playableTracks);
        this.trackCompleteListeners = new ArrayList<>();
        this.currentTrack = null;
        this.currentTrackIndex = 0;
    }

    public void changeTrack(final String resourcePath) {
        if (currentTrack != null) {
            currentTrack.stop();
        }

        currentTrack = playableTrackMap.get(resourcePath);
        currentTrackIndex = playQueue.indexOf(currentTrack);

        trackChangedListener.onTrackChanged(resourcePath);
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

    public void registerTrackCompleteListener(final ITrackCompleteListener trackCompleteListener) {
        trackCompleteListeners.add(trackCompleteListener);
    }

    public void cleanUp() {
        for (final PlayableTrack playableTrack : playableTracks) {
            playableTrack.stop();
        }
    }

    @Override
    public void onTrackComplete() {
        for (final ITrackCompleteListener trackCompletionListener : trackCompleteListeners) {
            trackCompletionListener.onTrackComplete();
        }
    }

    private List<PlayableTrack> createPlayableTracks(final List<String> resourcePaths) {
        final List<PlayableTrack> playableTracks = new ArrayList<>();

        for (final String resourcePath : resourcePaths) {
            final PlayableTrack playableTrack = new PlayableTrack(resourcePath, this.applicationContext);
            playableTrack.registerTrackCompleteListener(this);

            playableTracks.add(playableTrack);
            playableTrackMap.put(resourcePath, playableTrack);
        }

        return playableTracks;
    }
}
