package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class CredentialDto {
    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private boolean active;

    public Long getId() {
        return id;
    }

    public CredentialDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public CredentialDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFirstname() {
        return firstname;
    }

    public CredentialDto setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public CredentialDto setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public CredentialDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public CredentialDto setActive(boolean active) {
        this.active = active;
        return this;
    }
}
