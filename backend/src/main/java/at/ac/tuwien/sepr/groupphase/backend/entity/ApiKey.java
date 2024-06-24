package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class ApiKey {

    @Id
    @GeneratedValue
    private Long id;
    private String apikey;
    private String description;
    private Date created;

    public String getApiKey() {
        return apikey;
    }

    public ApiKey setApiKey(String key) {
        this.apikey = key;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ApiKey setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getCreated() {
        return created;
    }

    public ApiKey setCreated(Date created) {
        this.created = created;
        return this;
    }

    public long getId() {
        return id;
    }

    public ApiKey setId(long id) {
        this.id = id;
        return this;
    }


}
