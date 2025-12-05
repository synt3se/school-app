package com.github.synt3se.entity;

public enum NotificationType {
    LESSON_SCHEDULED,    // Новое занятие запланировано
    LESSON_CANCELLED,    // Занятие отменено
    LESSON_RESCHEDULED,  // Занятие перенесено
    LESSON_REMINDER,     // Напоминание о занятии
    GRADE_ADDED,         // Выставлена оценка
    PAYMENT_REQUIRED,    // Требуется оплата
    PAYMENT_RECEIVED     // Оплата получена
}
