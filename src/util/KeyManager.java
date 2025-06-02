package util;

import java.io.*;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyManager {

    private static final Logger LOGGER = Logger.getLogger(KeyManager.class.getName());

    public static KeyPair loadOrGenerateKeyPair(String privPath, String pubPath) {
        File priv = new File(privPath);
        File pub = new File(pubPath);
        try {
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
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Key loading or generation failed", e);
            throw new KeyManagerException("KeyPair 처리 중 오류 발생", e);
        }
    }

    public static PublicKey loadPublicKey(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (PublicKey) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "공개키 로딩 실패", e);
            throw new KeyManagerException("공개키 로딩 실패", e);
        }
    }

    public static PrivateKey loadPrivateKey(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (PrivateKey) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "개인키 로딩 실패", e);
            throw new KeyManagerException("개인키 로딩 실패", e);
        }
    }
}
