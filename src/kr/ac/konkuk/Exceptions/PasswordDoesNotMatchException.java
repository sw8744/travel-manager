package kr.ac.konkuk.Exceptions;


public class PasswordDoesNotMatchException extends RuntimeException {
    public PasswordDoesNotMatchException(String message) {
        super(message);
    }

    public PasswordDoesNotMatchException() {
        super("비밀번호가 다름");
    }
}
