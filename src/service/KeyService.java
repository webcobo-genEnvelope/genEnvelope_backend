package service;

import domain.KeyPairInfo;
import repository.KeyRepository;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class KeyService {

    private final KeyRepository repository = new KeyRepository();

    public KeyPairInfo createKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        KeyPair keyPair = generator.generateKeyPair();
        return new KeyPairInfo(keyPair.getPublic(), keyPair.getPrivate());
    }

    public void saveKeys(KeyPairInfo keyPairInfo, String publicPath, String privatePath) throws Exception {
        repository.saveKeyToFile(keyPairInfo.getPublicKey(), publicPath);
        repository.saveKeyToFile(keyPairInfo.getPrivateKey(), privatePath);
    }
}
