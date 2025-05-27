package koda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import koda.dto.request.DonationStoryModifyRequestDto;
import koda.dto.response.AreaCode;
import lombok.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

    @Column(name="area_code", length = 10)
    @Enumerated(EnumType.STRING) //AREA100,200,300
    private AreaCode areaCode;
    @Column(name="story_title", length = 600)
    private String storyTitle;

    @Column(name = "story_passcode",length = 60)
    private String storyPasscode;
    @Column(name="story_writer", length = 150)
    private String storyWriter;
    @Column(name="anonymity_flag", length = 1)
    private String anonymityFlag; //null 취급
    @Column(name="read_count")
    private Integer readCount;
    @Column(name="story_contents", columnDefinition = "TEXT")
    private String storyContents;

    @Column(name="file_name", length = 600)
    private String fileName;
    @Column(name="org_file_name", length = 600)
    private String orgFileName;

    @Column(name = "write_time", nullable = false, updatable = false)
    private LocalDateTime writeTime;
    @Column(name="writer_id", length = 60)
    private String writerId;  //null 취급

    @Column(name="modify_time", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP on UPDATE CURRENT_TIMESTAMP",
            insertable = false, updatable = false)
    private LocalDateTime modifyTime;

    @Column(name="modifier_id", length = 60)
    private String modifierId; //null 취급
    @Column(name="del_flag", length = 1, nullable = false)
    private String delFlag;

    @OneToMany(mappedBy="story", fetch= FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval=true)
    @JsonIgnore
    private List<DonationStoryComment> comments = new ArrayList<>();

    // private String donorName; 삭제

    @PrePersist
    protected void onCreate() {//엔티티가 처음 persist 되기 직전에 호출되는 메서드 (생성일자 설정)
        if (this.writeTime == null) {
            this.writeTime = LocalDateTime.now();
        }

        if (this.delFlag == null) {
            this.delFlag = "N";
        }
    }
    /*연관관계 편의 메서드*/
    public void addComment(DonationStoryComment comment){
        comments.add(comment); //
        comment.setStory(this);
    }
    public void removeComment(DonationStoryComment comment){
        comments.remove(comment);
        comment.setStory(null);
    }
    //조회수 증가
    public void increaseReadCount(){ //조회수 증가메서드
        this.readCount = (this.readCount == null) ? 1 : readCount + 1;
    }

    //수정때 이용
    public void modifyDonationStory(DonationStoryModifyRequestDto requestDto, String fileName, String orgFileName) {
        this.areaCode = requestDto.getAreaCode();
        this.storyTitle = requestDto.getStoryTitle();
        this.storyWriter = requestDto.getStoryWriter();
        this.storyContents = requestDto.getStoryContents();
        this.fileName = fileName;
        this.orgFileName = orgFileName;
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
