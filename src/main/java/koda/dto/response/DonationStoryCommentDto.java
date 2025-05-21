package koda.dto.response;

import koda.entity.DonationStoryComment;
import koda.entity.DonationStoryComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DonationStoryCommentDto {
    private String commentWriter; //추모자
    private String password;
    private String comments;
    private LocalDateTime commentWriteTime;

    public static DonationStoryCommentDto fromEntity(DonationStoryComment domainStoryComment) {
        return DonationStoryCommentDto.builder()
                .commentWriter(domainStoryComment.getCommentWriter())
                .password(domainStoryComment.getCommentPassword())
                .comments(domainStoryComment.getContents())
                .commentWriteTime(domainStoryComment.getWriteTime()).build();
    }
}
