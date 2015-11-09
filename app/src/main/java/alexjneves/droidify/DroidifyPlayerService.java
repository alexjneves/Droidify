package alexjneves.droidify;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public final class DroidifyPlayerService extends Service {
    public DroidifyPlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
