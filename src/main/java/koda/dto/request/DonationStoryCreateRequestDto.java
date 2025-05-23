package koda.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import koda.dto.response.AreaCode;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class DonationStoryCreateRequestDto {

    @NotNull
    private AreaCode areaCode;
    @NotBlank
    private String storyTitle;

    @NotBlank
    @Size(min=8)
    private String storyPasscode;
    @NotBlank
    private String storyWriter;
    private String storyContents;

    @NotNull
    private String captchaToken; // hCaptcha가 전달한 캡차 인증 값
    private MultipartFile file;
}
