package controller.exceptions;

/**
 * Created by stas on 11/11/17.
 */
public class ControllerException extends RuntimeException {

    private String message;

    public ControllerException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public ControllerException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
