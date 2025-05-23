package koda.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import koda.dto.request.*;
import koda.dto.response.AreaCode;
import koda.dto.response.DonationStoryListDto;
import koda.dto.response.DonationStoryDetailDto;
import koda.dto.response.DonationStoryWriteFormDto;
import koda.entity.DonationStory;
import koda.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.net.URI;
@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;

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
        List<AreaCode> areas = List.of(AreaCode.AREA100, AreaCode.AREA200, AreaCode.AREA300);
        if(areas.isEmpty()){
            throw new RuntimeException("사용 가능한 지역 코드가 없습니다.");
        }
        return DonationStoryWriteFormDto.builder()
                .areaOptions(areas).build();
    }

    /*
    기증 후 스토리 글쓰기 등록
     */
    public void createDonationStory(DonationStoryCreateRequestDto requestDto){
        String areaCode = requestDto.getAreaCode().toString();
        if(areaCode== null || areaCode.isBlank())
            throw new IllegalArgumentException("권역 선택은 필수입니다.");

        if(!(areaCode.equals("AREA100") || areaCode.equals("AREA200") || areaCode.equals("AREA300"))){
            throw new IllegalArgumentException("존재하지 않는 권역 코드입니다.");
        }
        if(requestDto.getStoryTitle() == null || requestDto.getStoryTitle().isBlank()){
            throw new IllegalArgumentException("제목은 필수 입력값입니다.");
        }
        if(requestDto.getStoryPasscode() == null || requestDto.getStoryPasscode().isBlank()){
            throw new IllegalArgumentException("비밀번호는 필수 입력값입니다.");
        }
        if(!validatePassword(requestDto.getStoryPasscode())){
            throw new RuntimeException("패스워드가 틀립니다.");
        }


        //캡차 검증 코드 추가해야 함.
        String storedFileName = null;
        String originalFileName = null;


        String contentType= requestDto.getFile().getContentType();
        if(contentType == null || !contentType.startsWith("image")){
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
        MultipartFile file = requestDto.getFile();
        if(file != null && !file.isEmpty()){
            if(requestDto.getFile() != null && !requestDto.getFile().isEmpty()){
                try{
                    originalFileName = requestDto.getFile().getOriginalFilename();
                    storedFileName = UUID.randomUUID().toString().replace("-", "").toUpperCase();

                    Path savePath = Paths.get("target/test-uploads",storedFileName); //파일 주소 얻기(일단 임시 주소)
                    Files.copy(requestDto.getFile().getInputStream(), savePath); //파일 저장
                } catch (IOException e) {
                    throw new RuntimeException("파일 업로드 실패", e);
                }
            }
        }

        DonationStory story = DonationStory.builder()
                .areaCode(requestDto.getAreaCode())
                .storyPasscode(requestDto.getStoryPasscode())
                .storyWriter(requestDto.getStoryWriter())
                .anonymityFlag(null)
                .readCount(0)
                .storyContents(requestDto.getStoryContents())
                .fileName(storedFileName)
                .orgFileName(originalFileName)
                .build();

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

        if(!validatePassword(verifyPassword.getStoryPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        if(storySeq.equals(verifyPassword.getStorySeq())){
            throw new IllegalArgumentException("MISMATCH_SEQ");
        }
        if(!verifyPassword.getStoryPassword().equals(story.getStoryPasscode())){
            throw new IllegalArgumentException("MISMATCH_PWD");
        }
    }

    @Transactional
    public void modifyDonationStory(Long storySeq, DonationStoryModifyRequestDto requestDto){
        DonationStory donationStory = donationRepository.findById(storySeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        String storedFileName = UUID.randomUUID().toString().replace("-", "").toUpperCase(); //uuid 처리
        if(validatePassword(requestDto.getStoryPassword())){
            donationStory.modifyDonationStory(requestDto, storedFileName );
        }
        throw new RuntimeException("비밀번호가 일치하지 않습니다.");

    }

    @Transactional
    public void deleteDonationStory(Long storySeq, VerifyStoryPasswordDto storyPasswordDto){

        DonationStory donationStory = donationRepository.findById(storySeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        if(!storyPasswordDto.getStoryPassword().equals(donationStory.getStoryPasscode())){ //비밀번호 불일치시
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        donationRepository.delete(donationStory);
    }




    //비밀번호 검증 (영 + 숫자 + 8자 ~ 16자 코드) - story, comment 모두 처리
    public boolean validatePassword(String password){
        if(password.matches("^[a-zA-Z0-9]{8,16}$")){
            return true;
        }
        return false;
    }

    //캡차 검증 ( 프론트에서 캡차 토큰을 받아 다시 검증하는 코드 , hcaptcha api 를 호출하여 토큰을 기반으로 검증
    public boolean verifyCaptcha(String token){
        String secret = "team-secret-key";
        String url = "https://hcaptcha.com/siteverify";

        try{
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString("secret=" + secret + "&response" + token))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> result =  mapper.readValue(response.body(), Map.class);

            return Boolean.TRUE.equals(result.get("success"));
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }


}
