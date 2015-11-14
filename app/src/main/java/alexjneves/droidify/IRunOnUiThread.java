package alexjneves.droidify;

public interface IRunOnUiThread {
    void executeOnUiThread(final Runnable toRun);
}
