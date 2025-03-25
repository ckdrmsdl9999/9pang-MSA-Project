package com._hateam.message.infrastructure.repository;

import com._hateam.message.domain.model.QSlackMessage;
import com._hateam.message.domain.model.SlackMessage;
import com._hateam.message.domain.repository.SlackMessageRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class SlackMessageRepositoryCustomImpl implements SlackMessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SlackMessage> searchMessages(String receiverId, LocalDateTime startDate,
                                             LocalDateTime endDate, String keyword, Pageable pageable) {
        QSlackMessage slackMessage = QSlackMessage.slackMessage;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(slackMessage.deletedAt.isNull());

        if (StringUtils.hasText(receiverId)) {
            builder.and(slackMessage.receiverId.eq(receiverId));
        }

        if (startDate != null) {
            builder.and(slackMessage.sentAt.goe(startDate));
        }

        if (endDate != null) {
            builder.and(slackMessage.sentAt.loe(endDate));
        }

        if (StringUtils.hasText(keyword)) {
            builder.and(slackMessage.content.containsIgnoreCase(keyword));
        }

        List<SlackMessage> content = queryFactory
                .selectFrom(slackMessage)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(slackMessage.sentAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(slackMessage.count())
                .from(slackMessage)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}