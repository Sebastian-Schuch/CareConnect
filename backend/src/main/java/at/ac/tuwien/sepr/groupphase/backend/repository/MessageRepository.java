package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByTreatment_IdOrderByTimestampAsc(Long treatmentId);

    @Query("SELECT m FROM Message m WHERE m.treatment.id IN ?1 AND m.timestamp = (SELECT MAX(m2.timestamp) FROM Message m2 WHERE m2.treatment.id = m.treatment.id)")
    List<Message> findLatestMessagesForTreatments(List<Long> treatmentIds);

}
