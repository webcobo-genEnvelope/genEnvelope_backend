package util;

import java.io.*;
import java.security.*;

public class KeyManager {
    private static final String KEY_FILE = "data/rsa.key";

    public static KeyPair loadOrGenerateKeyPair() {
        try {
            File file = new File(KEY_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    return (KeyPair) ois.readObject();
                }
            } else {
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                kpg.initialize(2048);
                KeyPair kp = kpg.generateKeyPair();
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                    oos.writeObject(kp);
                }
                return kp;
            }
        } catch (Exception e) {
            throw new RuntimeException("키 로딩/생성 실패: " + e.getMessage());
        }
    }
}