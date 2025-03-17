package com._hateam.common.aistudio.controller;

import com.ana29.deliverymanagement.externalApi.aistudio.GeminiService;
import com.ana29.deliverymanagement.externalApi.aistudio.dto.CreatedGeminiResponseDto;
import com.ana29.deliverymanagement.externalApi.aistudio.dto.GeminiRequestDto;
import com.ana29.deliverymanagement.global.dto.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class GeminiController {
    private final GeminiService geminiService;

    @PostMapping("/question")
    public ResponseEntity<ResponseDto<CreatedGeminiResponseDto>> geminiGetAnswer(@RequestBody @Valid GeminiRequestDto requestDto){
        CreatedGeminiResponseDto response = geminiService.generateContent(
            requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDto.success(HttpStatus.CREATED, response));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDto<Object>> deleteGemini(@RequestParam UUID id,
                                             @AuthenticationPrincipal UserDetails userDetails) {

        geminiService.softDeleteGemini(id, userDetails);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDto.success(HttpStatus.OK, null));
    }

    /*    ex)
{
    "contents": [
    {
        "parts": [
        {
            "text": "김치찌개 메뉴를 팔거야. 메뉴에 대한 설명을 적어줘."
        }
      ]
    }
  ]
}*/

}
