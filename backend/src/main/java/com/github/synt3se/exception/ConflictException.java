package com.github.synt3se.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public static ConflictException alreadyExists(String entity) {
        return new ConflictException(entity + " уже существует");
    }

    public static ConflictException alreadyEnrolled() {
        return new ConflictException("Ребёнок уже записан на это занятие");
    }

    public static ConflictException alreadyGraded() {
        return new ConflictException("Оценка уже выставлена");
    }

    public static ConflictException alreadyMarked() {
        return new ConflictException("Посещаемость уже отмечена");
    }
}
