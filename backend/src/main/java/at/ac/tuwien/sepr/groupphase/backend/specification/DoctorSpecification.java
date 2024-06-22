package at.ac.tuwien.sepr.groupphase.backend.specification;

import at.ac.tuwien.sepr.groupphase.backend.entity.Doctor;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.springframework.data.jpa.domain.Specification;

public class DoctorSpecification {

    public static Specification<Doctor> containsTextInAnyField(String searchText) {
        return (root, query, criteriaBuilder) -> {
            String searchPattern = "%" + searchText.toLowerCase() + "%";
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("credential").get("firstName")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("credential").get("lastName")), searchPattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("credential").get("email")), searchPattern)
            );
        };
    }

    public static Specification<Doctor> isActive() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("credential").get("active"));
    }
}
