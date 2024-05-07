package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class CredentialCreateDto {
    private String email;
    private String firstname;
    private String lastname;

    public String getEmail() {
        return email;
    }

    public CredentialCreateDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFirstname() {
        return firstname;
    }

    public CredentialCreateDto setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public CredentialCreateDto setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }
}
