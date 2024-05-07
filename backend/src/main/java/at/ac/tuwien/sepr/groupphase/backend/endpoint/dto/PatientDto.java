package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class PatientDto {
    private long uid;
    private int svnr;
    private String lastname;
    private String firstname;
    private String email;
    private String password;
    private boolean active;

    public long getUid() {
        return uid;
    }

    public PatientDto setUid(long uid) {
        this.uid = uid;
        return this;
    }

    public int getSvnr() {
        return svnr;
    }

    public PatientDto setSvnr(int svnr) {
        this.svnr = svnr;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public PatientDto setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public String getFirstname() {
        return firstname;
    }

    public PatientDto setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public PatientDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public PatientDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public PatientDto setActive(boolean active) {
        this.active = active;
        return this;
    }
    //private List<MedicineDto> medicines;
    //private List<AllergiesDto> allergies;
}
