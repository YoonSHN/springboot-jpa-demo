package koda.service;

import jakarta.transaction.Transactional;
import koda.dto.request.DonationCommentCreateRequestDto;
import koda.dto.request.DonationStoryCommentModifyRequestDto;
import koda.dto.request.VerifyCommentPasscodeDto;
import koda.entity.DonationStory;
import koda.entity.DonationStoryComment;
import koda.repository.DonationCommentRepository;
import koda.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DonationCommentService {

    private final DonationCommentRepository commentRepository;
    private final DonationRepository storyRepository;
    private final CaptchaService captchaService;

    @Transactional
    public void createDonationStoryComment(Long storySeq, DonationCommentCreateRequestDto requestDto) {
        DonationStory story = storyRepository.findById(storySeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        if (requestDto.getCommentWriter() == null || requestDto.getCommentWriter().isBlank()) {
            throw new IllegalArgumentException("댓글 작성자가 존재하지 않습니다.");
        }
        if (requestDto.getCommentPasscode() == null || requestDto.getCommentPasscode().isBlank()) {
            throw new IllegalArgumentException("비밀번호 입력은 필수입니다.");
        }
        //!captchaService.verifyCaptcha(requestDto.getCaptchaToken())
        if (false) {
            throw new IllegalArgumentException("캡차 인증 실패");
        }

        DonationStoryComment comment = DonationStoryComment.builder()
                .commentWriter(requestDto.getCommentWriter())
                .commentPasscode(requestDto.getCommentPasscode())
                .contents(requestDto.getContents())
                .writeTime(LocalDateTime.now())
                .writerId(null)
                .modifyTime(null)
                .delFlag("N")
                .build();

        story.addComment(comment); //연관관계 편의 메서드로 양방향 관계 설정

        commentRepository.save(comment);
    }

    @Transactional
    public void modifyDonationComment(Long commentSeq, DonationStoryCommentModifyRequestDto requestDto){
        DonationStoryComment storyComment = commentRepository.findById(commentSeq)
                .orElseThrow( () -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

        if(requestDto.getCommentWriter() == null || requestDto.getCommentWriter().isBlank()){
            throw new IllegalArgumentException("댓글 작성자가 존재하지 않습니다.");
        }
        //!captchaService.verifyCaptcha(requestDto.getCaptchaToken())
        if (false) {
            throw new IllegalArgumentException("캡차 인증 실패");
        }

        storyComment.modifyDonationStoryComment(requestDto);
    }

    public void deleteDonationComment(Long commentSeq, VerifyCommentPasscodeDto commentDto){
        DonationStoryComment storyComment = commentRepository.findById(commentSeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

        if(!commentDto.getCommentPasscode().equals(storyComment.getCommentPasscode())){
            throw new IllegalArgumentException("패스워드가 틀립니다.");
        }
        commentRepository.delete(storyComment);
    }




}
