package alexjneves.droidify;

/**
 * A simple interface designed to indicate a class which can execute Runnables on the UI thread.
 * Used when a class wishes to interact with the UI but has no application context in which to do so.
 */
public interface IRunOnUiThread {
    /**
     * Executes the runnable on the UI thread.
     *
     * @param toRun The runnable to be executed
     */
    void executeOnUiThread(final Runnable toRun);
}
