package koda.dto.response;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import koda.entity.DonationStory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DonationStoryListDto {
    @NotNull
    private Long storySeq;
    @NotBlank
    private String storyTitle;
    @NotBlank
    private String storyWriter;
    @NotNull
    @Min(0)
    private Integer readCount;
    @NotNull
    private LocalDateTime writeTime;

    public static DonationStoryListDto fromEntity(DonationStory story){ //정적 팩토리 메서드(DTO변환용)
        return DonationStoryListDto.builder()
                .storySeq(story.getStorySeq())
                .storyTitle(story.getStoryTitle())
                .storyWriter(story.getStoryWriter())
                .readCount(story.getReadCount() != null ? story.getReadCount() : 0)
                .writeTime(story.getWriteTime() != null ? story.getWriteTime() : LocalDateTime.now())
                .build();
    }
}
