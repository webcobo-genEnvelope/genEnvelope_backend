package domain;

import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyPairInfo {
    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public KeyPairInfo(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
