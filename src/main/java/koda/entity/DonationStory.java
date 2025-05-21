package koda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import koda.dto.request.DonationStoryModifyRequestDto;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="tb25_420_donation_story")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class DonationStory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storySeq;

    private String areaCode;
    private String storyTitle;

    @Column(name = "story_passcode")
    private String storyPassword;
    private String storyWriter;
    private String anonymityFlag; //null 취급
    private Integer readCount;
    private String storyContents;

    @Column(columnDefinition = "TEXT")
    private String fileName;
    @Column(columnDefinition = "TEXT")
    private String orgFileName;
    private LocalDateTime writeTime;
    private String writerId;  //null 취급

    @LastModifiedDate
    private LocalDateTime modifyTime;

    private String modifierId; //null 취급
    private String delFlag;

    @OneToMany(mappedBy="story", fetch= FetchType.LAZY)
    @JsonIgnore
    private List<DonationStoryComment> comments = new ArrayList<>();

    // private String donorName; 삭제

    public void increaseReadCount(){ //조회수 증가메서드
        this.readCount = (this.readCount == null) ? 1 : readCount + 1;
    }
    public void modifyDonationStory(DonationStoryModifyRequestDto requestDto, String fileName){
        this.areaCode = requestDto.getAreaCode();
        this.storyTitle = requestDto.getStoryTitle();
        this.storyPassword = requestDto.getStoryPassword();
        this.storyWriter = requestDto.getStoryWriter();
        this.storyContents = requestDto.getStoryContents();
        this.orgFileName = requestDto.getFile().getOriginalFilename();
        this.fileName = fileName;
    }


//    // JSON 처리용 getter/setter
//    public List<String> getFileNames() {
//        try {
//            return new ObjectMapper().readValue(fileNamesJson, new TypeReference<>() {});
//        } catch (Exception e) {
//            return List.of(); // 파싱 실패 시 빈 리스트 반환
//        }
//    }
//
//    public void setFileNames(List<String> fileNames) {
//        try {
//            this.fileNamesJson = new ObjectMapper().writeValueAsString(fileNames);
//        } catch (Exception e) {
//            this.fileNamesJson = "[]";
//        }
//    }


}
