package koda.dto.response;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import koda.entity.DonationStoryComment;
import koda.entity.DonationStory;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class DonationStoryDetailDto {
    @NotNull
    private Long storySeq;
    @NotBlank
    private String title;
    @NotBlank
    private String storyWriter;
    private String uploadDate;

    private AreaCode areaCode;
    private Integer readCount;
    private String storyContent;
    private String fileName;        // 저장된 파일 이름 (서버 파일명)
    private String orgFileName;

    private List<DonationStoryCommentDto> comments;

    public static DonationStoryDetailDto fromEntity(DonationStory story){
        return DonationStoryDetailDto.builder()
                .storySeq(story.getStorySeq())
                .title(story.getStoryTitle())
                .storyWriter(story.getStoryWriter())
                .uploadDate(story.getWriteTime().toLocalDate().toString())
                .areaCode(story.getAreaCode())
                .readCount(story.getReadCount())
                .storyContent(story.getStoryContents())
                .fileName(story.getFileName())
                .orgFileName(story.getOrgFileName())
                .comments(
                        story.getComments().stream()
                                .map(DonationStoryCommentDto::fromEntity)
                                .toList()
                ).build();
    }
}
