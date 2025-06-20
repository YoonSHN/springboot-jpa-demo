package koda.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationCommentCreateRequestDto {

    @NotBlank
    private String commentWriter;
    @NotNull
    private String commentPasscode;
    @NotBlank
    private String contents;
    @NotNull
    private String captchaToken;
}
