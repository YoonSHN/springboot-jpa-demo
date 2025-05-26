package koda.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import koda.dto.request.DonationStoryCreateRequestDto;
import koda.dto.response.AreaCode;
import koda.dto.response.DonationStoryListDto;
import koda.dto.response.DonationStoryWriteFormDto;
import koda.service.DonationCommentService;
import koda.service.DonationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DonationController.class)
public class DonationControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean DonationService donationService;
    @MockBean
    DonationCommentService commentService;

    @Test
    @DisplayName("스토리 전체 조회 - 성공")
    public void getAllDonationList_success() throws Exception { //donationLetters 에 접속시 200 오류 500이 잘 나오는지 + service호출시 DonationStoryListDto가 잘 반환되는지
        //given
        DonationStoryListDto dto = new DonationStoryListDto(
                1L, "제목1", "글쓴이1", 0, LocalDateTime.now()
        );
        List<DonationStoryListDto> content = List.of(dto);
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "storySeq"));
        Page<DonationStoryListDto> page = new PageImpl<>(content, pageable, 1);

        given(donationService.findAllDonationStories(any(Pageable.class))).willReturn(page);

        //when & then
        mockMvc.perform(get("/donationLetters").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("스토리 전체 조회 - 실패")
    public void getAllDonationList_failure() throws Exception{
        DonationStoryListDto dto1 = new DonationStoryListDto(1L, "제목1", "글쓴이1", 0, LocalDateTime.now());
        List<DonationStoryListDto> listDto = List.of(dto1);
//        given(donationService.findAllDonationStories()).willThrow(new RuntimeException(""));

        mockMvc.perform(get("/donationLetters")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("기증 후 스토리 목록 가져오기 실패"))
                .andExpect(jsonPath("$.error").value(""));

        System.out.println("donationSerivce = " + donationService);
    }

    @Test
    @DisplayName("스토리 작성 폼 출력 - 성공")
    public void getDonationWriteForm() throws Exception {
        DonationStoryWriteFormDto writeFormDto = new DonationStoryWriteFormDto(List.of(AreaCode.AREA100, AreaCode.AREA200));

        given(donationService.loadDonationStoryFormData()).willReturn(writeFormDto);

        mockMvc.perform(get("/donationLetters/new")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("스토리 등록 - 성공")
    public void createStory_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "hello world".getBytes()
        );
        DonationStoryCreateRequestDto requestDto = new DonationStoryCreateRequestDto(
                AreaCode.AREA100,"제목1","pdddceqceqd","작가1",
                "안녕하세요","ㅇwqkdoqwkdowqkdq",file);

        doNothing().when(donationService).createDonationStory(any(DonationStoryCreateRequestDto.class));
        mockMvc.perform(multipart("/donationLetters")
                .file(file)
                .param("areaCode", requestDto.getAreaCode().name())
                .param("storyTitle", requestDto.getStoryTitle())
                        .param("storyPasscode", requestDto.getStoryPasscode())
                        .param("storyContents", requestDto.getStoryContents())
                        .param("storyWriter", requestDto.getStoryWriter())
                        .param("captchaToken", requestDto.getCaptchaToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("스토리가 성공적으로 등록되었습니다."));
    }
    @Test
    @DisplayName("스토리 등록 - 실패")
    public void createStory_failure() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "hello world".getBytes()
        );
        DonationStoryCreateRequestDto requestDto = new DonationStoryCreateRequestDto(
                AreaCode.AREA100,"dqwdwqkodqdq","ddddqwdqdd","작가1",
                "안녕하세요","ㅇwqkdoqwkdowqkdq",file);

        doThrow(new IllegalArgumentException("예외 발생")).when(donationService).createDonationStory(any(DonationStoryCreateRequestDto.class));
        mockMvc.perform(multipart("/donationLetters")
                        .file(file)
                        .param("areaCode", "AREA100")
                        .param("storyTitle", "제목1")
                        .param("storyPasscode", requestDto.getStoryPasscode())
                        .param("storyWriter", requestDto.getStoryWriter())
                        .param("storyContents", requestDto.getStoryContents())
                        .param("captchaToken", requestDto.getCaptchaToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("필수 입력값이 누락되었습니다."))
                        .andExpect(jsonPath("$.success").value(false));
    }
    @Test
    public void getDonationStoryDetail() {

    }

    @Test
    public void verifyStoryPassword() {
    }

    @Test
    public void modifyStory() {
    }

    @Test
    public void deleteStory() {
    }

    @Test
    public void createComment() {
    }

    @Test
    public void modifyComment() {
    }

    @Test
    public void deleteComment() {
    }
}