package com._hateam.message.domain.repository;

import com._hateam.message.domain.model.SlackMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SlackMessageRepository extends JpaRepository<SlackMessage, UUID>, SlackMessageRepositoryCustom {

    @Query("SELECT sm FROM SlackMessage sm WHERE sm.messageId = :messageId AND sm.deletedAt IS NULL")
    Optional<SlackMessage> findByIdNotDeleted(@Param("messageId") UUID messageId);

    @Query("SELECT sm FROM SlackMessage sm WHERE sm.deletedAt IS NULL")
    Page<SlackMessage> findAllNotDeleted(Pageable pageable);
}