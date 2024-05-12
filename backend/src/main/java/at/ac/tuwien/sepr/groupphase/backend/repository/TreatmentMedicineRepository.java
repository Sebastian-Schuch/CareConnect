package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.TreatmentMedicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentMedicineRepository extends JpaRepository<TreatmentMedicine, Long> {
}
