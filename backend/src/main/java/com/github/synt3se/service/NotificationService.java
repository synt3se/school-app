package com.github.synt3se.service;

import com.github.synt3se.dto.response.NotificationResponse;
import com.github.synt3se.dto.response.UnreadCountResponse;
import com.github.synt3se.entity.*;
import com.github.synt3se.exception.NotFoundException;
import com.github.synt3se.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Page<NotificationResponse> getNotifications(UUID userId, Boolean unreadOnly, Pageable pageable) {
        Page<Notification> notifications;

        if (Boolean.TRUE.equals(unreadOnly)) {
            notifications = notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc(userId, false, pageable);
        } else {
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        return notifications.map(this::toResponse);
    }

    public UnreadCountResponse getUnreadCount(UUID userId) {
        long count = notificationRepository.countByUserIdAndRead(userId, false);
        return UnreadCountResponse.builder().count(count).build();
    }

    @Transactional
    public void markAsRead(UUID userId, UUID notificationId) {
        int updated = notificationRepository.markAsRead(notificationId, userId);
        if (updated == 0) {
            throw new NotFoundException("Уведомление не найдено");
        }
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsRead(userId);
    }

    @Transactional
    public void notifyGradeAdded(Child child, Grade grade) {
        if (child.getParent() == null) return;

        Notification notification = Notification.builder()
                .user(child.getParent())
                .type(NotificationType.GRADE_ADDED)
                .title("Новая оценка")
                .message(String.format("%s получил(а) оценку %d по предмету \"%s\"",
                        child.getFullName(),
                        grade.getValue(),
                        grade.getLesson().getCourse().getName()))
                .grade(grade)
                .lesson(grade.getLesson())
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyLessonCancelled(User user, Lesson lesson) {
        Notification notification = Notification.builder()
                .user(user)
                .type(NotificationType.LESSON_CANCELLED)
                .title("Занятие отменено")
                .message(String.format("Занятие \"%s\" (%s) было отменено",
                        lesson.getCourse().getName(),
                        lesson.getTopic()))
                .lesson(lesson)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyLessonRescheduled(User user, Lesson fromLesson, Lesson toLesson) {
        Notification notification = Notification.builder()
                .user(user)
                .type(NotificationType.LESSON_RESCHEDULED)
                .title("Занятие перенесено")
                .message(String.format("Занятие \"%s\" перенесено на %s",
                        fromLesson.getCourse().getName(),
                        toLesson.getStartTime().toString()))
                .lesson(toLesson)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyPaymentReceived(User user, Payment payment) {
        Notification notification = Notification.builder()
                .user(user)
                .type(NotificationType.PAYMENT_RECEIVED)
                .title("Оплата получена")
                .message(String.format("Оплата за курс \"%s\" на сумму %.2f руб. успешно проведена",
                        payment.getCourse().getName(),
                        payment.getAmount()))
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyLessonReminder(User user, Lesson lesson) {
        Notification notification = Notification.builder()
                .user(user)
                .type(NotificationType.LESSON_REMINDER)
                .title("Напоминание о занятии")
                .message(String.format("Завтра в %s занятие \"%s\" - %s",
                        lesson.getStartTime().toLocalTime(),
                        lesson.getCourse().getName(),
                        lesson.getTopic()))
                .lesson(lesson)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.getRead())
                .lessonId(notification.getLesson() != null ? notification.getLesson().getId() : null)
                .gradeId(notification.getGrade() != null ? notification.getGrade().getId() : null)
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

