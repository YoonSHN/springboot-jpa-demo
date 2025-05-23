package koda.repository;


import koda.entity.DonationStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DonationRepository extends JpaRepository<DonationStory, Long> {

}
