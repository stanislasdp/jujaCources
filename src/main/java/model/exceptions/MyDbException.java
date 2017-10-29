package model.exceptions;

/**
 * Created by stas on 10/16/17.
 */
public class MyDbException extends RuntimeException {

    private String message;

    public MyDbException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public MyDbException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
