package koda.dto.response;


import koda.entity.DonationStoryComment;
import koda.entity.DonationStory;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class DonationStoryDetailDto {

    private String title;
    private String storyWriter;
    private String uploadDate;

    private AreaCode areaCode;
    private Integer readCount;
    private String storyContent;

    private List<DonationStoryCommentDto> comments;

    public static DonationStoryDetailDto fromEntity(DonationStory story){
        return DonationStoryDetailDto.builder()
                .title(story.getStoryTitle())
                .storyWriter(story.getStoryWriter())
                .uploadDate(story.getWriteTime().toLocalDate().toString())
                .areaCode(story.getAreaCode())
                .readCount(story.getReadCount())
                .storyContent(story.getStoryContents())
                .comments(
                        story.getComments().stream()
                                .map(DonationStoryCommentDto::fromEntity)
                                .toList()
                ).build();
    }
}
