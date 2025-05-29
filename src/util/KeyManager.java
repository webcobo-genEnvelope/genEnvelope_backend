package util;

import java.io.*;
import java.security.*;

public class KeyManager {
    public static KeyPair loadOrGenerateKeyPair(String privPath, String pubPath) {
        try {
            File priv = new File(privPath);
            File pub = new File(pubPath);
            if (priv.exists() && pub.exists()) {
                try (ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(priv));
                     ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(pub))) {
                    return new KeyPair((PublicKey) ois2.readObject(), (PrivateKey) ois1.readObject());
                }
            } else {
                KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
                gen.initialize(2048);
                KeyPair kp = gen.generateKeyPair();
                try (ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream(priv));
                     ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream(pub))) {
                    oos1.writeObject(kp.getPrivate());
                    oos2.writeObject(kp.getPublic());
                }
                return kp;
            }
        } catch (Exception e) {
            throw new RuntimeException("키 로딩 실패: " + e.getMessage());
        }
    }

    public static PublicKey loadPublicKey(String path) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (PublicKey) ois.readObject();
        }
    }
}