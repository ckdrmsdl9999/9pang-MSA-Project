package com._hateam.message.domain.service;

import com._hateam.message.domain.model.SlackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlackMessageDomainService {

    /**
     * 메시지가 유효한지 확인
     *
     * @param content 메시지 내용
     * @return 유효성 여부
     */
    public boolean isValidMessage(String content) {
        return content != null && !content.trim().isEmpty() && content.length() <= 4000;
    }

    /**
     * 슬랙 ID가 유효한지 확인
     *
     * @param slackId 슬랙 ID
     * @return 유효성 여부
     */
    public boolean isValidSlackId(String slackId) {
        // 슬랙 ID 유효성 검사 로직
        // 일반적으로 슬랙 ID는 "U" 또는 "B"로 시작하며 영문자와 숫자로 구성
        // 사용자(U), 봇(B), 채널(C), DM 채널(D)로 시작하는 ID 허용
        return slackId != null && slackId.matches("^[UBCD][A-Z0-9]{8,}$");
    }

    /**
     * 메시지 발송 상태 업데이트
     *
     * @param message   메시지 엔티티
     * @param isSuccess 발송 성공 여부
     * @return 업데이트된 메시지 엔티티
     */
    public SlackMessage updateMessageStatus(SlackMessage message, boolean isSuccess) {
        if (isSuccess) {
            message.updateStatus(SlackMessage.MessageStatus.SENT);
        } else {
            message.updateStatus(SlackMessage.MessageStatus.FAILED);
        }
        return message;
    }
}