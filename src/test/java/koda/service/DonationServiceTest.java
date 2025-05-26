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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

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

        Pageable pageable = PageRequest.of(0, 20);
        Page<DonationStory> page = new PageImpl<>(donationStories, pageable, 1);

        given(repository.findAll(pageable)).willReturn(page);

       //when
       Page<DonationStoryListDto> list = service.findAllDonationStories(pageable);

       assertEquals(2, list.getContent().size());



    }

    @Test
    public void loadDonationStoryFormData() { //권역, captcha 검증
        //given
        DonationStoryWriteFormDto dto = service.loadDonationStoryFormData();

        assertEquals(AreaCode.AREA100, dto.getAreaOptions().get(0));
        assertEquals(3, dto.getAreaOptions().size());
        assertIterableEquals(List.of(AreaCode.AREA100, AreaCode.AREA200, AreaCode.AREA300),
                dto.getAreaOptions());

    }

    @Test
    public void createDonationStory() throws IOException { //폼데이터가 잘 전달이 되는가?, repository에 잘 save되었는가?
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "hello world".getBytes()
        );
        DonationStoryCreateRequestDto requestDto = new DonationStoryCreateRequestDto(AreaCode.AREA100,"제목1",
                "wdqw","작가1" ,"안녕하세요","dqwokdpqdokq",file);

        Path uploadPath = Paths.get("target/test-uploads");
        Files.createDirectories(uploadPath);
        service.createDonationStory(requestDto);

        ArgumentCaptor<DonationStory> captor = ArgumentCaptor.forClass(DonationStory.class);
        verify(repository, times(1)).save(captor.capture());

        DonationStory saved = captor.getValue();

        assertEquals(requestDto.getAreaCode(), saved.getAreaCode());
        assertEquals(requestDto.getStoryContents(), "안녕하세요");
        assertEquals(requestDto.getStoryPasscode(),saved.getStoryPasscode());

    }

    @Test
    public void findDonationStory() {
    }
}