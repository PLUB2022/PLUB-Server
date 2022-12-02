package plub.plubserver.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import plub.plubserver.domain.account.config.AuthCode;
import plub.plubserver.domain.account.exception.AuthException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Component
public class CustomEncryptUtil {

    private byte[] key;
    private SecretKeySpec secretKeySpec;

    public CustomEncryptUtil(@Value("${aes.secret-key}") String rawKey) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = rawKey.getBytes(StandardCharsets.UTF_8);
            key = sha.digest(key);
            key = Arrays.copyOf(key, 24);
            secretKeySpec = new SecretKeySpec(key, "AES");
        } catch (Exception ignored) {
        }
    }

    public String encrypt(String str) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return encodeBase64(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new AuthException(AuthCode.ENCRYPTION_FAILURE);
        }
    }

    public String decrypt(String str) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(decodeBase64(str)));
        } catch (Exception e) {
            throw new AuthException(AuthCode.DECRYPTION_FAILURE);
        }
    }

    private String encodeBase64(byte[] source) {
        return Base64.getEncoder().encodeToString(source);
    }

    private byte[] decodeBase64(String encodedString) {
        return Base64.getDecoder().decode(encodedString);
    }
}