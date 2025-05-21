package koda.service;

import koda.dto.response.DonationStoryListDto;
import koda.dto.response.DonationStoryWriteFormDto;
import koda.entity.DonationStory;
import koda.repository.AfterDonationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTest {

    @Mock
    private AfterDonationRepository repository;

    @InjectMocks
    private DonationService service;

    @Test
    public void findAllDonationStories() {
       DonationStory story1 = DonationStory.builder()
               .storyTitle("제목1")
               .storyWriter("작가1")
               .readCount(10)
               .writeTime(LocalDateTime.now())
               .build();
        DonationStory story2 = DonationStory.builder()
                .storyTitle("제목2")
                .storyWriter("작가2")
                .readCount(10)
                .writeTime(LocalDateTime.now())
                .build();

        List<DonationStory> donationStories = List.of(story1, story2);

        given(repository.findAll()).willReturn(donationStories);

       //when
        List<DonationStoryListDto> list = service.findAllDonationStories();

        assertEquals(2, list.size());
        assertEquals("제목1", list.get(0).getStoryTitle());

    }

    @Test
    public void loadDonationStoryFormData() { //권역, captcha 검증
        //given
        DonationStoryWriteFormDto dto = service.loadDonationStoryFormData();

        assertEquals("1권역(수도권, 강원, 제주)", dto.getAreaOptions().get(0));
        assertEquals("", dto.getCaptchaImageUrl());
        assertTrue(dto.getAreaOptions().get(0).contains("1권역"));
        assertEquals(3, dto.getAreaOptions().size());
        assertIterableEquals(List.of("1권역(수도권, 강원, 제주)", "2권역(충청, 전라)", "3권역(영남)"),
                dto.getAreaOptions());

        assertNotNull(dto.getCaptchaImageUrl());

    }

    @Test
    public void createDonationStory() { //폼데이터가 잘 전달이 되는가?, repository에 잘 save되었는가?
    }

    @Test
    public void findDonationStory() {
    }
}