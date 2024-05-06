package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class PatientCreateDto {
    private int svnr;
    private String lastname;
    private String firstname;
    private String email;

    public int getSvnr() {
        return svnr;
    }

    public PatientCreateDto setSvnr(int svnr) {
        this.svnr = svnr;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public PatientCreateDto setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public String getFirstname() {
        return firstname;
    }

    public PatientCreateDto setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public PatientCreateDto setEmail(String email) {
        this.email = email;
        return this;
    }
    //private List<MedicineDto> medicines;
    //private List<AllergiesDto> allergies;
}
