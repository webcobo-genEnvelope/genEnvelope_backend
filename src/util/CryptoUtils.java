package util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class CryptoUtils {

    private static final Logger LOGGER = Logger.getLogger(CryptoUtils.class.getName());

    public static byte[] hash(byte[] data) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(data);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Hashing algorithm not found", e);
            throw new CryptoException("SHA-256 해시 처리 중 오류 발생", e);
        }
    }

    public static byte[] sign(byte[] data, PrivateKey key) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(key);
            sig.update(data);
            return sig.sign();
        } catch (GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "Signing failed", e);
            throw new CryptoException("서명 생성 중 오류 발생", e);
        }
    }

    public static byte[] encryptKeyWithRSA(SecretKey key, PublicKey pubKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return cipher.doFinal(key.getEncoded());
        } catch (GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE, "RSA key encryption failed", e);
            throw new CryptoException("RSA 키 암호화 중 오류 발생", e);
        }
    }
}
