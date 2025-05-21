package koda.service;

import jakarta.transaction.Transactional;
import koda.dto.request.CommentCreateRequestDto;
import koda.dto.request.DonationStoryCommentModifyRequestDto;
import koda.dto.request.VerifyCommentPasswordDto;
import koda.entity.DonationStory;
import koda.entity.DonationStoryComment;
import koda.repository.AfterDonationRepository;
import koda.repository.DonationCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DonationCommentService {

    private final DonationCommentRepository commentRepository;
    private final AfterDonationRepository storyRepository;
    @Transactional
    public void createDonationStoryComment(Long storySeq, CommentCreateRequestDto requestDto) {
        DonationStory story = storyRepository.findById(storySeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        if (requestDto.getCommentWriter() == null || requestDto.getCommentWriter().isBlank()) {
            throw new IllegalArgumentException("댓글 작성자가 존재하지 않습니다.");
        }
        if (requestDto.getCommentPassword() == null || requestDto.getCommentPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호 입력은 필수입니다.");
        }

        DonationStoryComment comment = DonationStoryComment.builder()
                .story(story)
                .commentWriter(requestDto.getCommentWriter())
                .commentPassword(requestDto.getCommentPassword())
                .contents(requestDto.getContents())
                .writeTime(LocalDateTime.now())
                .writerId(null)
                .modifyTime(null)
                .delFlag("N")
                .build();

        commentRepository.save(comment);
    }

    @Transactional
    public void modifyDonationComment(Long commentSeq, DonationStoryCommentModifyRequestDto requestDto){
        DonationStoryComment storyComment = commentRepository.findById(commentSeq)
                .orElseThrow( () -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

        if(requestDto.getCommentWriter() == null || requestDto.getCommentWriter().isBlank()){
            throw new IllegalArgumentException("댓글 작성자가 존재하지 않습니다.");
        }
        if(requestDto.getCommentPassword() == null || requestDto.getCommentPassword().isBlank()){
            throw new IllegalArgumentException("비밀번호 입력은 필수입니다.");
        }

        storyComment.modifyDonationStoryComment(requestDto);
    }

    public void deleteDonationComment(Long commentSeq, VerifyCommentPasswordDto commentDto){
        DonationStoryComment storyComment = commentRepository.findById(commentSeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

        if(commentDto.getCommentPassword() != storyComment.getCommentPassword()){
            throw new IllegalArgumentException("패스워드가 틀립니다.");
        }
        commentRepository.delete(storyComment);
    }




}
