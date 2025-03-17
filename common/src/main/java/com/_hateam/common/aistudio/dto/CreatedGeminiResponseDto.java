package com._hateam.common.aistudio.dto;

import com.ana29.deliverymanagement.externalApi.aistudio.entity.Gemini;

import java.util.UUID;

public record CreatedGeminiResponseDto (String request, String response, UUID id) {

	public static CreatedGeminiResponseDto from(Gemini gemini) {
		return new CreatedGeminiResponseDto(gemini.getQuestion(), gemini.getAnswer(), gemini.getId());
	}
}
