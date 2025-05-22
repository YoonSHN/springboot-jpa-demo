package koda.repository;

import koda.entity.DonationStoryComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationCommentRepository extends JpaRepository<DonationStoryComment, Long> {


}

