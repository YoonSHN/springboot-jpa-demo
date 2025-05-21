package koda.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateRequestDto {

    private Long commentSeq;
    private String commentWriter;
    private String commentPassword;
    private String contents;
}
