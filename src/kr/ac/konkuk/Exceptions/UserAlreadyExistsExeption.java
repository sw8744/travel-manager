package kr.ac.konkuk.Exceptions;

public class UserAlreadyExistsExeption extends RuntimeException{
    public UserAlreadyExistsExeption() {
        super("유저가 이미 존재함");
    }
}
