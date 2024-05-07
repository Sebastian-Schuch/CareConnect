package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record PatientCreateDto(
    String svnr,
    String lastname,
    String firstname,
    String email
) {
    public CredentialCreateDto toCredentialCreateDto() {
        return new CredentialCreateDto(email, firstname, lastname);
    }
}
