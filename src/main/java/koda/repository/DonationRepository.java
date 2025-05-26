package koda.repository;



import koda.dto.response.DonationStoryDetailDto;
import koda.entity.DonationStory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface DonationRepository extends JpaRepository<DonationStory, Long> {

    @Query(
            """
            SELECT new koda.dto.response.DonationStoryListDto(u.storySeq, u.storyTitle, u.storyWriter, u.readCount, u.writeTime) 
            FROM DonationStory u ORDER BY u.storySeq DESC
            """
    )
    Page<DonationStory> findAllDonationStories(Pageable pageable);

    @Query(
            """
            SELECT s
            FROM DonationStory s
            LEFT JOIN FETCH s.comments
            WHERE s.storySeq =:storySeq
            """
    )
    Optional<DonationStory> findWithCommentsById(Long storySeq);
}
