package koda.controller;

import koda.dto.response.DonationStoryListDto;
import koda.service.DonationService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @Autowired private MockMvc mockMvc;
    @MockBean DonationService donationService;

    @Test
    @DisplayName("스토리 전체 조회")
    public void getAllDonationList() throws Exception { //donationLetters 에 접속시 200 오류 500이 잘 나오는지 + service호출시 DonationStoryListDto가 잘 반환되는지
        DonationStoryListDto dto1 = new DonationStoryListDto(1L, "제목1", "글쓴이1", 0, LocalDateTime.now());
        List<DonationStoryListDto> listDto = List.of(dto1);

        given(donationService.findAllDonationStories()).willReturn(listDto); // 저 메서드가 호출된다면 listDto를 반환하도록 설정

        mockMvc.perform(get("/donationLetters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].title").value("제목1"));

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