package at.ac.tuwien.sepr.groupphase.backend.specification;

import at.ac.tuwien.sepr.groupphase.backend.entity.Medication;
import org.springframework.data.jpa.domain.Specification;

public class MedicationSpecification {

    public static Specification<Medication> nameContains(String searchTerm) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + searchTerm.toLowerCase() + "%");
    }

    public static Specification<Medication> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }
}
