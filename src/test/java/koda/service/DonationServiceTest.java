package koda.service;


import koda.dto.request.DonationStoryCreateRequestDto;
import koda.dto.request.DonationStoryModifyRequestDto;
import koda.dto.request.VerifyStoryPasscodeDto;
import koda.dto.response.AreaCode;
import koda.dto.response.DonationStoryDetailDto;
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


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import java.util.Optional;

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
                "file", "test.png", "image/png", "hello, world".getBytes()
        );
        DonationStoryCreateRequestDto requestDto = new DonationStoryCreateRequestDto(AreaCode.AREA100,"제목1",
                "fekofqkfq", "작가1", "안녕하세요", "ㅇㅈㅈㅇㅂ",file);
        Path uploadPath = Paths.get(".target/test-uploads");
        Files.createDirectories(uploadPath);
        service.createDonationStory(requestDto);

        ArgumentCaptor<DonationStory> captor = ArgumentCaptor.forClass(DonationStory.class);
        verify(repository, times(1)).save(captor.capture());

        DonationStory saved = captor.getValue();

        assertEquals(requestDto.getAreaCode(), saved.getAreaCode());
        assertEquals(requestDto.getStoryContents() , "안녕하세요");
        assertEquals(requestDto.getStoryPasscode(), saved.getStoryPasscode());
    }

    @Test
    public void findDonationStory_success() {
        // given

        Long storySeq = 1L;
        DonationStory story = DonationStory.builder()
                .storySeq(storySeq)
                .storyTitle("제목1")
                .storyWriter("작성자1")
                .writeTime(LocalDateTime.now())
                .areaCode(AreaCode.AREA100)
                .readCount(10)
                .storyContents("내용입니다")
                .fileName("abc123.jpg")
                .orgFileName("원본.jpg")
                .comments(new ArrayList<>())
                .build();

        when(repository.findWithCommentsById(storySeq)).thenReturn(Optional.of(story));
        //when

        DonationStoryDetailDto detailDto = service.findDonationStory(storySeq);

        assertEquals(detailDto.getStorySeq(),story.getStorySeq());
        assertEquals("제목1", detailDto.getTitle());
        assertEquals("작성자1", detailDto.getStoryWriter());
        assertEquals("AREA100", detailDto.getAreaCode().toString());
        assertNotNull(detailDto);
    }
    @Test
    public void modifyDonationStory(){
        Long storySeq = 1L;
        DonationStory story = DonationStory.builder()
                .storySeq(storySeq)
                .storyTitle("제목1")
                .storyWriter("작성자1")
                .writeTime(LocalDateTime.now())
                .areaCode(AreaCode.AREA100)
                .readCount(10)
                .storyContents("내용입니다")
                .fileName("abc123.jpg")
                .orgFileName("원본.jpg")
                .comments(new ArrayList<>())
                .build();

        when(repository.findById(storySeq)).thenReturn(Optional.of(story));

        DonationStoryModifyRequestDto modifyDto = DonationStoryModifyRequestDto.builder()
                .areaCode(AreaCode.AREA200)
                .storyTitle("제목제목1").build();

        service.modifyDonationStory(storySeq, modifyDto);

        assertEquals("AREA200", story.getAreaCode().toString());
        assertEquals("제목제목1", story.getStoryTitle());


    }
    @Test
    public void deleteDonationStory(){
        Long storySeq = 1L;
        DonationStory story = DonationStory.builder()
                .storySeq(storySeq)
                .storyTitle("제목1")
                .storyPasscode("12345678")
                .storyWriter("작성자1")
                .writeTime(LocalDateTime.now())
                .areaCode(AreaCode.AREA100)
                .readCount(10)
                .storyContents("내용입니다")
                .fileName("abc123.jpg")
                .orgFileName("원본.jpg")
                .comments(new ArrayList<>())
                .build();

        VerifyStoryPasscodeDto passcodeDto = new VerifyStoryPasscodeDto("12345678");
        when(repository.findById(storySeq)).thenReturn(Optional.of(story));

        service.deleteDonationStory(storySeq, passcodeDto);

        assertDoesNotThrow(
                () -> repository.findById(2L));
        assertNotNull(story.getStoryPasscode());
        assertEquals(Integer.valueOf(10), story.getReadCount());
        verify(repository, times(1)).delete(story);

        ArgumentCaptor<DonationStory> captor = ArgumentCaptor.forClass(DonationStory.class);
        verify(repository).delete(captor.capture());
        assertEquals(storySeq, captor.getValue().getStorySeq());
    }
}