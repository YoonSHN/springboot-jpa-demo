package koda.repository;


import koda.entity.DonationStory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AfterDonationRepository extends JpaRepository<DonationStory, Long> {
}
