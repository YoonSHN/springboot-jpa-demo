package koda.service;

import com.mysql.cj.Session;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import koda.dto.request.DonationStoryCreateRequestDto;
import koda.dto.response.AreaCode;
import koda.dto.response.DonationStoryListDto;
import koda.dto.response.DonationStoryWriteFormDto;
import koda.entity.DonationStory;
import koda.repository.DonationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTest {

    @Mock
    private DonationRepository repository;

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

        assertEquals(AreaCode.AREA100, dto.getAreaOptions().get(0));
        assertEquals(3, dto.getAreaOptions().size());
        assertIterableEquals(List.of(AreaCode.AREA100, AreaCode.AREA200, AreaCode.AREA300),
                dto.getAreaOptions());

    }private AreaCode areaCode;
    @NotBlank
    private String storyTitle;

    @NotBlank
    @Min(8)
    private String storyPasscode;
    @NotBlank
    private String storyWriter;
    private String storyContents;

    @NotNull
    private String captchaToken; // hCaptcha가 전달한 캡차 인증 값
    private MultipartFile file;

    @Test
    public void createDonationStory() { //폼데이터가 잘 전달이 되는가?, repository에 잘 save되었는가?
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "hello world".getBytes()
        );
        DonationStoryCreateRequestDto requestDto = new DonationStoryCreateRequestDto(AreaCode.AREA100,"제목1",
                "1234a","작가1" ,"안녕하세요","dqwokdpqdokq",file);


    }

    @Test
    public void findDonationStory() {
    }
}