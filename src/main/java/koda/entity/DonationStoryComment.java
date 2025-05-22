package koda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import koda.dto.request.DonationStoryCommentModifyRequestDto;
import koda.dto.request.DonationStoryModifyRequestDto;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="tb25_421_donation_story_comment")
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class DonationStoryComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentSeq;

    @ManyToOne
    @JoinColumn(name = "story_seq")
    @JsonIgnore
    @ToString.Exclude
    private DonationStory story;

    private String commentWriter;

    private String commentPassword;

    private String contents;
    private LocalDateTime writeTime;
    private String writerId;

    @LastModifiedDate
    private LocalDateTime modifyTime;

    private String modifierId;
    private String delFlag;

    public void modifyDonationStoryComment(DonationStoryCommentModifyRequestDto requestDto) {
        this.commentWriter = requestDto.getCommentWriter();
        this.commentPassword = requestDto.getCommentPassword();
        this.contents = requestDto.getCommentContents();
    }

}
