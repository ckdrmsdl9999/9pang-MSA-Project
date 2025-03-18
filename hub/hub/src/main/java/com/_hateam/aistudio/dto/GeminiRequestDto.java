package com._hateam.aistudio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequestDto {
    private List<Content> contents;

    /**
     * 주어진 지역(region)에 대한 프롬프트 메시지를 설정합니다.
     * 프롬프트 형식:
     * "{region} 의 지도 정보가 필요해. 답변은 정확히 'name:ex1, address:ex2, latitude:ex3, longitude:ex4'의 형식으로만 답변해."
     *
     * @param region 해당 지역명
     */
    public void setPrompt(String region) {
        String promptText = region + " 시청의 지도 정보가 필요해. 답변은 정확히 'name:ex1, address:ex2, latitude:ex3, longitude:ex4'의 형식으로만 답변해.";
        Part part = Part.builder().text(promptText).build();
        Content content = Content.builder().parts(Collections.singletonList(part)).build();
        this.contents = Collections.singletonList(content);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Content {
        private List<Part> parts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Part {
        private String text;
    }
}
