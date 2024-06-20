package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.AdminDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChangePasswordDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CredentialDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DoctorDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PatientDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SecretaryDtoCreate;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.CredentialsMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Credential;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.CredentialRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.PdfService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserCreationFacadeService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.type.Role;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Random;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final CredentialsMapper mapper;
    private final UserCreationFacadeService userCreationFacadeService;
    private final PdfService pdfService;

    @Value("${PASSWORD_PEPPER}")
    private String passwordPepper;

    @Autowired
    public CustomUserDetailService(CredentialRepository credentialRepository, PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer,
                                   CredentialsMapper mapper, UserCreationFacadeService userCreationFacadeService, PdfService pdfService) {
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.mapper = mapper;
        this.userCreationFacadeService = userCreationFacadeService;
        this.pdfService = pdfService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            Credential applicationUser = findApplicationUserByEmail(email);

            List<GrantedAuthority> grantedRoles = AuthorityUtils.createAuthorityList(applicationUser.getRole().toString());

            return new User(applicationUser.getEmail(),
                applicationUser.getPassword(),
                applicationUser.getActive(),
                true,
                true,
                true,
                grantedRoles);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public Credential findApplicationUserByEmail(String email) {
        LOGGER.debug("Find application user by email");
        Credential applicationUser = credentialRepository.findByEmail(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());
        //TODO add locking mechanism in sprint 2, see issue #40
        //if (userDetails != null && userDetails.isAccountNonExpired() && userDetails.isAccountNonLocked() && userDetails.isCredentialsNonExpired() && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())) {
        if (userDetails != null && passwordEncoder.matches(userLoginDto.getPassword() + passwordPepper, userDetails.getPassword())) {
            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        }
        throw new BadCredentialsException("Username or password is incorrect or account is no longer active");
    }

    @Override
    public PDDocument createDoctor(DoctorDtoCreate toCreate) {
        UserLoginDto userLogin = createCredentials(mapper.doctorCreateDtoToCredentialCreateDto(toCreate));
        Credential credential = createCredentialEntity(mapper.doctorCreateDtoToCredentialCreateDto(toCreate), userLogin, Role.DOCTOR);
        DoctorDto doctorDto = userCreationFacadeService.createUser(toCreate, credential);
        userLogin.setId(doctorDto.id());
        return pdfService.getAccountDataSheet(userLogin);
    }

    @Override
    public PDDocument createSecretary(SecretaryDtoCreate toCreate) {
        UserLoginDto userLogin = createCredentials(mapper.secretaryCreateDtoToCredentialCreateDto(toCreate));
        Credential credential = createCredentialEntity(mapper.secretaryCreateDtoToCredentialCreateDto(toCreate), userLogin, Role.SECRETARY);
        SecretaryDto secretaryDetailDto = userCreationFacadeService.createUser(toCreate, credential);
        userLogin.setId(secretaryDetailDto.id());
        return pdfService.getAccountDataSheet(userLogin);
    }

    @Override
    public PDDocument createPatient(PatientDtoCreate toCreate) {
        UserLoginDto userLogin = createCredentials(mapper.patientCreateDtoToCredentialCreateDto(toCreate));
        Credential credential = createCredentialEntity(mapper.patientCreateDtoToCredentialCreateDto(toCreate), userLogin, Role.PATIENT);
        PatientDto patientDto = userCreationFacadeService.createUser(toCreate, credential);
        userLogin.setId(patientDto.id());
        return pdfService.getAccountDataSheet(userLogin);
    }

    @Override
    public PDDocument createAdministrator(AdminDtoCreate toCreate) {
        UserLoginDto userLogin = createCredentials(mapper.administratorCreateDtoToCredentialCreateDto(toCreate));
        Credential credential = createCredentialEntity(mapper.administratorCreateDtoToCredentialCreateDto(toCreate), userLogin, Role.ADMIN);
        AdminDto adminDto = userCreationFacadeService.createUser(toCreate, credential);
        userLogin.setId(adminDto.id());
        return pdfService.getAccountDataSheet(userLogin);
    }

    @Override
    public boolean isValidRequestOfRole(Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(role.name()));
    }

    /**
     * Create a credentials with the given data.
     *
     * @param toCreate the data of the user for the credential entity
     * @return the users login data
     */
    private UserLoginDto createCredentials(CredentialDtoCreate toCreate) {
        // Generate a random password
        String randomPassword = generateRandomPassword();


        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setEmail(toCreate.email());
        userLoginDto.setPassword(randomPassword);

        return userLoginDto;

    }

    /**
     * Create a credentials entity with the given data.
     *
     * @param toCreate    the data of the user for the credential entity
     * @param credentials the login data of the user
     * @param role        the role of the user
     * @return the users login data
     */
    private Credential createCredentialEntity(CredentialDtoCreate toCreate, UserLoginDto credentials, Role role) {
        // Encode the random password
        String encodedPassword = passwordEncoder.encode(credentials.getPassword() + passwordPepper);

        // Save the user with the encoded password
        Credential credential = new Credential();
        credential.setEmail(toCreate.email());
        credential.setFirstName(toCreate.firstname());
        credential.setLastName(toCreate.lastname());
        credential.setPassword(encodedPassword);
        credential.setActive(true);
        credential.setRole(role);
        credential.setInitialPassword(true);

        return credential;
    }

    @Override
    public void changePassword(ChangePasswordDto passwords) {
        LOGGER.trace("changePassword({})", passwords);
        Credential credential = findApplicationUserByEmail(passwords.email());
        if (passwordEncoder.matches(passwords.oldPassword() + passwordPepper, credential.getPassword())) {
            credential.setPassword(passwordEncoder.encode(passwords.newPassword() + passwordPepper));
            credential.setInitialPassword(false);
            credentialRepository.save(credential);
        } else {
            throw new ConflictException("Old password is incorrect");
        }
    }

    @Override
    public PDDocument resetPassword(String email) {
        LOGGER.trace("resetPassword({})", email);
        Credential credential = findApplicationUserByEmail(email);
        UserLoginDto userLogin = new UserLoginDto();
        userLogin.setEmail(credential.getEmail());
        userLogin.setPassword(this.generateRandomPassword());
        userLogin.setId(credential.getId());
        credential.setPassword(passwordEncoder.encode(userLogin.getPassword() + passwordPepper));
        credential.setInitialPassword(true);
        credentialRepository.save(credential);
        return pdfService.getAccountDataSheet(userLogin);
    }

    /**
     * Generate a random password of length 12.
     *
     * @return the random password
     */
    private String generateRandomPassword() {
        // Generate a random password of length 12
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+*#_-$%";
        StringBuilder randomPassword = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            randomPassword.append(characters.charAt(random.nextInt(characters.length())));
        }
        return randomPassword.toString();
    }

    @Override
    public void disableUser(String email) {
        LOGGER.trace("disableUser({})", email);
        Credential credential = findApplicationUserByEmail(email);
        credential.setActive(false);
        credentialRepository.save(credential);
    }
}
