package de.ait.platform.users.exceptions;

public class UserNotFound extends RuntimeException{
    public UserNotFound (String message) {
        super(message);
    }
}
