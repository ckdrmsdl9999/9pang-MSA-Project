package com._hateam.message.domain.repository;

import com._hateam.message.domain.model.SlackMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SlackMessageRepositoryCustom {
    Page<SlackMessage> searchMessages(String receiverId, LocalDateTime startDate,
                                      LocalDateTime endDate, String keyword, Pageable pageable);
}