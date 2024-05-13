package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class StationDto {
    private Long id;
    private String name;
    private Long capacity;

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getCapacity() {
        return capacity;
    }
}
