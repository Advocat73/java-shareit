package ru.practicum.shareit.exception;

public class DataConflictException extends RuntimeException {
    public DataConflictException(final String message) {
        super(message);
    }
}
