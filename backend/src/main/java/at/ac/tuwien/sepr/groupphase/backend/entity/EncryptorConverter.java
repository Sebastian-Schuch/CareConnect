package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.AttributeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Base64;

@Component
public class EncryptorConverter implements AttributeConverter<String, String> {

    private ThreadLocal<Key> key = new ThreadLocal<Key>() {
        @Override
        protected Key initialValue() {
            return new SecretKeySpec(secret.getBytes(), "AES");
        }
    };

    private ThreadLocal<Cipher> cipher = new ThreadLocal<Cipher>() {
        @Override
        protected Cipher initialValue() {
            try {
                return Cipher.getInstance("AES");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    @Value("${ENCRYPTION_SECRET}")
    private String secret;

    @Override
    public String convertToDatabaseColumn(String s) {
        try {
            cipher.get().init(Cipher.ENCRYPT_MODE, key.get());
            return Base64.getEncoder().encodeToString(cipher.get().doFinal(s.getBytes()));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String s) {
        try {
            cipher.get().init(Cipher.DECRYPT_MODE, key.get());
            return new String(cipher.get().doFinal(Base64.getDecoder().decode(s)));
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalStateException(e);
        }
    }
}
