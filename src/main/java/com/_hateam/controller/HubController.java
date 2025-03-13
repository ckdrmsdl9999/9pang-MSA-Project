package com._hateam.controller;

import com._hateam.dto.HubCreateRequestDto;
import com._hateam.dto.HubDto;
import com._hateam.global.dto.ResponseDto;
import com._hateam.service.HubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/hubs")
@RequiredArgsConstructor
public class HubController {

    private final HubService hubService;

    /**
     * 새로운 허브 게시글 생성
     * 예시: POST /hub/posts
     */
    @PostMapping
    public ResponseEntity<ResponseDto<HubDto>> createPost(
            @RequestBody @Valid HubCreateRequestDto requestDto) {

        HubDto createdPost = hubService.createPost(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success(HttpStatus.CREATED, createdPost));
    }

    /**
     * 특정 게시글 상세 조회
     * 예시: GET /hub/posts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<HubDto>> getPost(@PathVariable Long id) {
        HubDto post = hubService.getPost(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, post));
    }
    /**
     * 전체 허브 게시글 목록 조회 (페이지네이션 지원)
     * 예시: GET /hub/posts?page=0&size=10&sortBy=createdAt&isAsc=false
     */
    @GetMapping
    public ResponseEntity<ResponseDto<List<HubDto>>> getAllPosts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {

        List<HubDto> posts = hubService.getAllPosts(page, size, sortBy, isAsc);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, posts));
    }


    /**
     * 특정 게시글 수정
     * 예시: PATCH /hub/posts/{id}
     */
    @PatchMapping("/posts/{id}")
    public ResponseEntity<ResponseDto<HubDto>> updatePost(
            @PathVariable Long id,
            @RequestBody @Valid HubUpdateRequestDto requestDto) {

        HubDto updatedPost = hubService.updatePost(id, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, updatedPost));
    }

    /**
     * 특정 게시글 삭제
     * 예시: DELETE /hub/posts/{id}
     */
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ResponseDto<String>> deletePost(@PathVariable Long id) {
        hubService.deletePost(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, "Post deleted successfully"));
    }
}
