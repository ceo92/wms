package exception;

public class WmsException extends RuntimeException {

    public WmsException() {
        super();
    }

    public WmsException(String message) {
        super(message);
    }

    public WmsException(String message, Throwable cause) {
        super(message, cause);
    }

    public WmsException(Throwable cause) {
        super(cause);
    }
}
