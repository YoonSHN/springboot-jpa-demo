package koda.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import koda.entity.DonationStory;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationStoryCommentModifyRequestDto {
    @NotBlank
    private String commentWriter;
    @NotBlank
    private String commentContents;
    @NotBlank
    private String captchaToken;

}
