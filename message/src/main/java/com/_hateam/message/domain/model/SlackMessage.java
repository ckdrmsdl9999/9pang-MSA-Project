package com._hateam.message.domain.model;

import com._hateam.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_slack_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SlackMessage extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "message_id", columnDefinition = "uuid")
    private UUID messageId;

    @Column(name = "receiver_id", nullable = false)
    private String receiverId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    public enum MessageStatus {
        PENDING,
        SENT,
        FAILED
    }

    public void updateStatus(MessageStatus status) {
        this.status = status;
        if (status == MessageStatus.SENT) {
            this.sentAt = LocalDateTime.now();
        }
    }
}