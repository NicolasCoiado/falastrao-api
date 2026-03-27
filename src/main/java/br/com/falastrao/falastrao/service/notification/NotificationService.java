package br.com.falastrao.falastrao.service.notification;

import br.com.falastrao.falastrao.dto.response.NotificationResponse;
import br.com.falastrao.falastrao.dto.response.PageResponse;
import br.com.falastrao.falastrao.exception.NotFoundException;
import br.com.falastrao.falastrao.model.Notification;
import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.model.enums.NotificationOrigin;
import br.com.falastrao.falastrao.repository.NotificationRepository;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MessageSource messageSource;

    public NotificationService(NotificationRepository notificationRepository,
                               MessageSource messageSource) {
        this.notificationRepository = notificationRepository;
        this.messageSource = messageSource;
    }

    @Transactional
    public void sendSystemNotification(User recipient, String messageKey, Object[] args) {
        Locale locale = Locale.forLanguageTag(recipient.getLocale());
        String text = messageSource.getMessage(messageKey, args, locale);

        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setOrigin(NotificationOrigin.SYSTEM);
        notification.setText(text);
        notificationRepository.save(notification);
    }

    @Transactional
    public void sendUserNotification(User recipient, String senderUsername, String text) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setOrigin(NotificationOrigin.USER);
        notification.setSenderUsername(senderUsername);
        notification.setText(text);
        notificationRepository.save(notification);
    }

    public PageResponse<NotificationResponse> getNotifications(User user, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return PageResponse.from(
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                        .map(this::toResponse)
        );
    }

    public long countUnread(User user) {
        return notificationRepository.countByUserIdAndReadFalse(user.getId());
    }

    @Transactional
    public NotificationResponse markAsRead(UUID externalId, User user) {
        Notification notification = notificationRepository
                .findByExternalIdAndUserId(externalId, user.getId())
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        notification.setRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getExternalId(),
                notification.getOrigin(),
                notification.getSenderUsername(),
                notification.getText(),
                notification.isRead(),
                notification.getCreatedAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        );
    }
}