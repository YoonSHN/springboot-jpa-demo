package koda.service;

import jakarta.persistence.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import koda.dto.request.*;
import koda.dto.response.DonationStoryListDto;
import koda.dto.response.DonationStoryDetailDto;
import koda.dto.response.DonationStoryWriteFormDto;
import koda.entity.DonationStory;
import koda.entity.DonationStoryComment;
import koda.repository.AfterDonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DonationService {


    private final AfterDonationRepository donationRepository;
    // private final CaptchaService captchaService; 개발예정

    /*
    기증 후 스토리 게시글 출력
     */
    public List<DonationStoryListDto> findAllDonationStories() {
        return donationRepository.findAll() //엔티티 -> DTO로 변환
                .stream()
                .map(DonationStoryListDto::fromEntity)
                .toList();
    }

    /*
    기증 후 스토리 글쓰기 폼 데이터 출력
     */
    public DonationStoryWriteFormDto loadDonationStoryFormData(){
        List<String> areas = List.of(
                "1권역(수도권, 강원, 제주)",
                "2권역(충청, 전라)",
                "3권역(영남)"
        );

        //String captchaUrl = captchaService.generateCaptcha(session);
        String captchaUrl="";

        return DonationStoryWriteFormDto.builder()
                .areaOptions(areas)
                .captchaImageUrl(captchaUrl).build();
    }

    /*
    기증 후 스토리 글쓰기 등록
     */
    public void createDonationStory(DonationStoryCreateRequestDto requestDto, HttpSession session){
        String areaCode = requestDto.getAreaCode();
        if(areaCode== null || areaCode.isBlank())
            throw new IllegalArgumentException("권역 선택은 필수입니다.");
        if(!(areaCode.equals("AREA100") || areaCode.equals("AREA200") || areaCode.equals("AREA300"))){
            throw new IllegalArgumentException("존재하지 않는 권역 코드입니다.");
        }
        if(requestDto.getStoryTitle() == null || requestDto.getStoryTitle().isBlank()){
            throw new IllegalArgumentException("제목은 필수 입력값입니다.");
        }
        if(requestDto.getStoryPassword() == null || requestDto.getStoryPassword().isBlank()){
            throw new IllegalArgumentException("비밀번호는 필수 입력값입니다.");
        }

        //캡차 검증 코드 추가해야 함.
        String storedFileName = null;
        String originalFileName = null;

        if(requestDto.getFile() != null && !requestDto.getFile().isEmpty()){
            try{
                originalFileName = requestDto.getFile().getOriginalFilename();
                storedFileName = UUID.randomUUID().toString().replace("-", "").toUpperCase();

                Path savePath = Paths.get("/uploads",storedFileName); //파일 주소 얻기(일단 임시 주소)
                Files.copy(requestDto.getFile().getInputStream(), savePath); //파일 저장
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 실패", e);
            }
        }
        DonationStory story = DonationStory.builder()
                .areaCode(requestDto.getAreaCode())
                .storyPassword(requestDto.getStoryPassword())
                .storyWriter(requestDto.getStoryWriter())
                .anonymityFlag(null)
                .readCount(0)
                .storyContents(requestDto.getStoryContents())
                .fileName(storedFileName)
                .orgFileName(originalFileName)
                .writeTime(LocalDateTime.now())
                .writerId(null)
                .modifyTime(null)
                .modifierId(null)
                .delFlag("N").build();

        donationRepository.save(story);
    }
    /*
    기증 후 스토리 상세 조회
     */
    @Transactional
    public DonationStoryDetailDto findDonationStory(Long storySeq){
        DonationStory storyDetailStory = donationRepository.findById(storySeq)
                .orElseThrow( () -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        storyDetailStory.increaseReadCount(); //조회수 증가

        return DonationStoryDetailDto.fromEntity(storyDetailStory);

    }

    public void verifyPasswordWithPassword(Long storySeq, VerifyStoryPasswordDto verifyPassword) {
        DonationStory story = donationRepository.findById(storySeq)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND"));

        if(storySeq.equals(verifyPassword.getStorySeq())){
            throw new IllegalArgumentException("MISMATCH_SEQ");
        }
        if(!verifyPassword.getStoryPassword().equals(story.getStoryPassword())){
            throw new IllegalArgumentException("MISMATCH_PWD");
        }
    }

    @Transactional
    public void modifyDonationStory(Long storySeq, DonationStoryModifyRequestDto requestDto){
        DonationStory donationStory = donationRepository.findById(storySeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        String storedFileName = UUID.randomUUID().toString().replace("-", "").toUpperCase(); //uuid 처리

        donationStory.modifyDonationStory(requestDto, storedFileName );

    }

    @Transactional
    public void deleteDonationStory(Long storySeq, VerifyStoryPasswordDto storyPasswordDto){

        DonationStory donationStory = donationRepository.findById(storySeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        if(!storyPasswordDto.getStoryPassword().equals(donationStory.getStoryPassword())){ //비밀번호 불일치시
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        donationRepository.delete(donationStory);
    }




//    //비밀번호 검증 (영 + 숫자 + 8자 코드) - story, comment 모두 처리
//    public void validatePassword(){
//
//    }


}
