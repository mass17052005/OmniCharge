package com.omnicharge.notificationservice.service;

import com.omnicharge.notificationservice.config.RabbitMQConfig;
import com.omnicharge.notificationservice.dto.PaymentEvent;
import com.omnicharge.notificationservice.entity.Notification;
import com.omnicharge.notificationservice.enums.NotificationType;
import com.omnicharge.notificationservice.exception.ResourceNotFoundException;
import com.omnicharge.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_SUCCESS_QUEUE)
    public void handlePaymentSuccess(PaymentEvent event) {
        log.info("Notification: Payment SUCCESS for recharge {}",
                event.getRechargeId());

        Notification notification = Notification.builder()
                .userId(event.getUserId())
                .rechargeId(event.getRechargeId())
                .message("Your recharge of ID " + event.getRechargeId()
                        + " was successful! Transaction ref: " + event.getTxnRef())
                .type(NotificationType.SUCCESS)
                .build();

        notificationRepository.save(notification);
        log.info("Notification saved for user: {}", event.getUserId());
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_FAILED_QUEUE)
    public void handlePaymentFailed(PaymentEvent event) {
        log.info("Notification: Payment FAILED for recharge {}",
                event.getRechargeId());

        Notification notification = Notification.builder()
                .userId(event.getUserId())
                .rechargeId(event.getRechargeId())
                .message("Your recharge of ID " + event.getRechargeId()
                        + " failed. Please try again. Transaction ref: "
                        + event.getTxnRef())
                .type(NotificationType.FAILED)
                .build();

        notificationRepository.save(notification);
        log.info("Notification saved for user: {}", event.getUserId());
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getNotificationsByRechargeId(Long rechargeId) {
        return notificationRepository.findByRechargeId(rechargeId);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found: " + id));
    }
}
