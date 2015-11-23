package alexjneves.droidify.service;

final class FailedToRetrieveMediaException extends Exception {
    public FailedToRetrieveMediaException(final String detailMessage) {
        super(detailMessage);
    }
}
