package util;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;

public class KeyManager {
    private static final String LAB_PRIVATE_PATH = "data/labPrivate";
    private static final String LAB_PUBLIC_PATH = "data/labPublic";
    private static final String USER_PUBLIC_PATH = "data/public";

    public static KeyPair loadOrGenerateLabKeyPair() {
        try {
            if (Files.exists(Path.of(LAB_PRIVATE_PATH)) && Files.exists(Path.of(LAB_PUBLIC_PATH))) {
                PrivateKey privateKey = loadPrivateKey(LAB_PRIVATE_PATH);
                PublicKey publicKey = loadPublicKey(LAB_PUBLIC_PATH);
                return new KeyPair(publicKey, privateKey);
            }

            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            KeyPair kp = gen.generateKeyPair();
            Files.write(Path.of(LAB_PRIVATE_PATH), kp.getPrivate().getEncoded());
            Files.write(Path.of(LAB_PUBLIC_PATH), kp.getPublic().getEncoded());
            return kp;
        } catch (Exception e) {
            throw new RuntimeException("키 로딩/생성 실패: " + e.getMessage());
        }
    }

    public static PrivateKey loadPrivateKey(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(Path.of(path));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    public static PublicKey loadPublicKey(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(Path.of(path));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    public static class UserPublicKey {
        private final PublicKey publicKey;
        public UserPublicKey(PublicKey publicKey) {
            this.publicKey = publicKey;
        }
        public PublicKey getPublicKey() {
            return publicKey;
        }
    }

    public static UserPublicKey loadUserPublicKey() {
        try {
            PublicKey publicKey = loadPublicKey(USER_PUBLIC_PATH);
            return new UserPublicKey(publicKey);
        } catch (Exception e) {
            throw new RuntimeException("사용자 공개키 로드 실패: " + e.getMessage());
        }
    }
}
