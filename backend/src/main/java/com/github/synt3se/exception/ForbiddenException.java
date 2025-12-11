package com.github.synt3se.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public static ForbiddenException notYourResource(String resource) {
        return new ForbiddenException("У вас нет доступа к этому" + resource);
    }

    public static ForbiddenException notYourLesson() {
        return new ForbiddenException("Вы не ведёте это занятие");
    }

    public static ForbiddenException notYourChild() {
        return new ForbiddenException("Это не ваш ребёнок");
    }
}
