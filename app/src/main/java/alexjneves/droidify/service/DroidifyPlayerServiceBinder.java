package alexjneves.droidify.service;

import android.os.Binder;

public final class DroidifyPlayerServiceBinder extends Binder {
    private final IDroidifyPlayer droidifyPlayer;

    public DroidifyPlayerServiceBinder(final IDroidifyPlayer droidifyPlayer) {
        this.droidifyPlayer = droidifyPlayer;
    }

    public IDroidifyPlayer getDroidifyPlayer() {
        return droidifyPlayer;
    }
}
