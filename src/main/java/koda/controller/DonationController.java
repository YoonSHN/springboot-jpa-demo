package koda.controller;

import jakarta.servlet.http.HttpSession;
import koda.dto.request.*;
import koda.dto.response.DonationStoryDetailDto;
import koda.dto.response.DonationStoryListDto;
import koda.service.DonationCommentService;
import koda.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;
    private final DonationCommentService donationCommentService;

    @GetMapping("/donationLetters")
    public ResponseEntity<?> getAllDonationList() {
        try {
            List<DonationStoryListDto> lists = donationService.findAllDonationStories();
            System.out.println("서비스 호출 완료");

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "기증 후 스토리 목록 가져오기 성공",
                    "data", lists));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "기증 후 스토리 목록 가져오기 실패",
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/donationLetters/new")
    public ResponseEntity<?> getDonationWriteForm() {
        return ResponseEntity.ok(donationService.loadDonationStoryFormData());
    }

    @PostMapping(value = "/donationLetters", consumes = "multipart/form-data")
    public ResponseEntity<?> createStory(@ModelAttribute DonationStoryCreateRequestDto dto, HttpSession session) throws Exception {
        try {
            donationService.createDonationStory(dto, session);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "status", 201,
                    "message", "스토리가 성공적으로 등록되었습니다."
            ));
        } catch (IllegalArgumentException ie) {
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "code", 400,
                    "message", "필수 입력값이 누락되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "code", 500,
                    "message", "서버 내부 오류가 발생했습니다."
            ));
        }
    }

    @GetMapping("/donationLetters/{storySeq}")
    public ResponseEntity<?> getDonationStoryDetail(@PathVariable("storySeq") Long storySeq) {
        DonationStoryDetailDto donationDetailStory = donationService.findDonationStory(storySeq);

        return ResponseEntity.ok(Map.of(
                "status", "200",
                "message", "detail 페이지 출력",
                "data", donationDetailStory
        ));
    }

    @PostMapping("/donationLetters/{storySeq}/verifyPwd")
    public ResponseEntity<?> verifyStoryPassword(@PathVariable("storySeq") Long storySeq,
                                                 @RequestBody VerifyStoryPasswordDto dto) {
        try {
            donationService.verifyPasswordWithPassword(storySeq, dto);

            return ResponseEntity.ok(Map.of(
                    "result", 1,
                    "message", "비밀번호가 일치합니다."
            ));
        } catch (IllegalArgumentException e) {
            String message = switch (e.getMessage()) {
                case "NOT_FOUND" -> "해당 게시글이 존재하지 않습니다.";
                case "MISMATCH_SEQ" -> "잘못된 접근입니다.";
                case "MISMATCH_PWD" -> "비밀번호가 일치하지 않습니다.";
                default -> "알 수 없는 오류가 발생했습니다.";
            };
            return ResponseEntity.status(400).body(Map.of(
                    "result", 0,
                    "message", message
            ));
        }

    }

    /*
    기증 후 스토리 수정
     */
    @PatchMapping("/donationLetters/{storySeq}")
    public ResponseEntity<?> modifyStory(@PathVariable("storySeq") Long storySeq, @RequestBody DonationStoryModifyRequestDto requestDto) {
        try {
            donationService.modifyDonationStory(storySeq, requestDto);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "code", 201,
                    "message", "스토리가 성공적으로 수정되었습니다."
            ));
        } catch (IllegalArgumentException ie) {
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "code", 400,
                    "message", "필수 입력값이 누락되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "code", 500,
                    "message", "서버 내부 오류가 발생했습니다."
            ));
        }
    }

    /*
    가증 후 스토리 스토리 삭제
     */
    @DeleteMapping("/donationLetters/{storySeq}")
    public ResponseEntity<?> deleteStory(@PathVariable("storySeq") Long storySeq, @RequestBody VerifyStoryPasswordDto storyPasswordDto) {
        try {
            donationService.deleteDonationStory(storySeq, storyPasswordDto);
            return ResponseEntity.ok(Map.of(
                    "result", 1,
                    "message", "스토리가 정상적으로 삭제 되었습니다."
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "result", 0,
                    "message", "비밀번호가 일치하지 않습니다."
            ));
        }
    }

    /*
    기증 후 스토리 댓글 등록
     */
    @PostMapping("/donationLetters/{storySeq}/comments")
    public ResponseEntity<?> createComment(@PathVariable("storySeq") Long storySeq, @RequestBody CommentCreateRequestDto requestDto) {
        try {
            donationCommentService.createDonationStoryComment(storySeq, requestDto);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "code", 201,
                    "message", "편지 댓글이 성공적으로 등록되었습니다."
            ));
        } catch (IllegalArgumentException ie) {
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "code", 400,
                    "message", "필수 입력값이 누락되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "code", 500,
                    "message", "서버 내부 오류가 발생했습니다."
            ));
        }
    }

    @PatchMapping("/donationLetters/{storySeq}/comments/{commentSeq}")
    public ResponseEntity<?> modifyComment(@PathVariable("storySeq") Long storySeq
            ,@PathVariable("commentSeq")Long commentSeq
            ,@RequestBody DonationStoryCommentModifyRequestDto requestDto) {
        try{
            donationCommentService.modifyDonationComment(commentSeq,requestDto);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "code", 201,
                    "message", "스토리 댓글이 성공적으로 수정되었습니다."
            ));
        }catch(IllegalArgumentException ie){
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "code", 400,
                    "message", "필수 입력값이 누락되었습니다."
            ));
        }catch(Exception e){
            return ResponseEntity.status(500).body(Map.of(
                    "success",false,
                    "code", 500,
                    "message", "서버 내부 오류가 발생했습니다."
            ));
        }
    }
    @DeleteMapping("/donationLetters/{storySeq}/comments/{commentSeq}")
    public ResponseEntity<?> deleteComment(@PathVariable("storySeq") Long storySeq,
                                           @PathVariable("commentSeq") Long commentSeq,
                                           @RequestBody VerifyCommentPasswordDto commentPassword){
        try{
            donationCommentService.deleteDonationComment(commentSeq, commentPassword);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "code", 201,
                    "message", "스토리 댓글이 성공적으로 삭제되었습니다."
            ));
        }catch(IllegalArgumentException ie){
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "code", 400,
                    "message", "필수 입력값이 누락되었습니다."
            ));
        }catch(Exception e){
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "code", 500,
                    "message", "서버 내부 오류가 발생했습니다."
            ));
        }
    }
}
