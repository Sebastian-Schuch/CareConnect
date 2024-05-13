package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class StationDtoCreate {

    @NotNull
    @NotEmpty
    @Size(max = 255, message = "Name is too long")
    private String name;

    @Min(value = 0, message = "Capacity must be greater than 0")
    private Long capacity;

    public StationDtoCreate() {
    }

    public StationDtoCreate(String name, Long capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public Long getCapacity() {
        return capacity;
    }
}
