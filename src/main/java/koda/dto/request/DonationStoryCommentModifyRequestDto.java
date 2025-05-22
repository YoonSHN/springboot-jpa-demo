package koda.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import koda.entity.DonationStory;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonationStoryCommentModifyRequestDto {


    private String commentWriter;
    private String commentPassword;
    private String commentContents;
    private String modifyTime;

}
