package koda.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class DonationStoryCreateRequestDto {

    private String areaCode;
    private String storyTitle;

    private String storyPasscode;
    private String storyWriter;
    private String storyContents;

    private String captchaToken; // hCaptcha가 전달한 캡차 인증 값
    private MultipartFile file;
}
