package kz.course.exceptions;

public class RequestExistException extends RuntimeException {
    public RequestExistException(String message) {
        super(message);
    }
}
