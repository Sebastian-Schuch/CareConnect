package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class AllergyDto {
    public Long uid;
    public String name;

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public AllergyDto() {
    }

    public AllergyDto(Long uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }
}
