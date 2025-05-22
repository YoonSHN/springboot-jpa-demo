package koda.controller;

import koda.dto.response.DonationStoryListDto;
import koda.service.DonationCommentService;
import koda.service.DonationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        DonationStoryListDto dto1 = new DonationStoryListDto(1L, "제목1", "글쓴이1", 0, LocalDateTime.now());
        List<DonationStoryListDto> listDto = List.of(dto1);

        given(donationService.findAllDonationStories()).willReturn(listDto); // 저 메서드가 호출된다면 listDto를 반환하도록 설정

        mockMvc.perform(get("/donationLetters")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].storyTitle").value("제목1"));
        System.out.println("donationService = " + donationService);
    }
    @Test
    @DisplayName("스토리 전체 조회 - 실패")
    public void getAllDonationList_failure() throws Exception{
        DonationStoryListDto dto1 = new DonationStoryListDto(1L, "제목1", "글쓴이1", 0, LocalDateTime.now());
        List<DonationStoryListDto> listDto = List.of(dto1);
        given(donationService.findAllDonationStories()).willThrow(new RuntimeException("ㅎㅇ"));

        mockMvc.perform(get("/donationLetters")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("ㅎㅇ"));

        System.out.println("donationSerivce = " + donationService);
    }

    @Test
    public void getDonationWriteForm() {
    }

    @Test
    public void createStory() {
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