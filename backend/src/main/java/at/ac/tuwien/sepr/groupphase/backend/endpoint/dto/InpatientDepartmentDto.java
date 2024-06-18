package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record InpatientDepartmentDto(
    Long id,
    String name,
    int capacity,
    boolean active
) {
}
