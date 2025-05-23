package koda.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class DonationStoryCreateRequestDto {

    @NotNull
    private AreaCode areaCode;
    @NotBlank
    private String storyTitle;

    @NotBlank
    private String storyPasscode;
    @NotBlank
    private String storyWriter;
    private String storyContents;

    @NotNull
    private String captchaToken; // hCaptcha가 전달한 캡차 인증 값
    private MultipartFile file;
}
