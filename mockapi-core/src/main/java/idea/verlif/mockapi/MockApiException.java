package idea.verlif.mockapi;

public class MockApiException extends RuntimeException {
    public MockApiException() {
    }

    public MockApiException(String message) {
        super(message);
    }

    public MockApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public MockApiException(Throwable cause) {
        super(cause);
    }

    public MockApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
