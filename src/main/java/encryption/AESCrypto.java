
package encryption;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AESCrypto {
    private static SecretKey aesKey;
    private static final String SALT = "a9v5n38s";

    public static void initKeyFromPassword(String masterPassword) throws Exception {
        byte[] saltBytes = SALT.getBytes(StandardCharsets.UTF_8);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), saltBytes, 65536, 128);
        SecretKey tmp = factory.generateSecret(spec);
        aesKey = new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivParams);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decrypt(String encryptedData) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedData);

        byte[] iv = new byte[16];
        byte[] ciphertext = new byte[combined.length - 16];
        System.arraycopy(combined, 0, iv, 0, 16);
        System.arraycopy(combined, 16, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
        byte[] original = cipher.doFinal(ciphertext);

        return new String(original, StandardCharsets.UTF_8);
    }
}
