package koda.dto.request;

import koda.dto.response.AreaCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DonationStoryModifyRequestDto {

    private AreaCode areaCode;
    private String storyTitle;

    private String storyPassword;
    private String storyWriter;
    private String storyContents;

    private MultipartFile file;
}
