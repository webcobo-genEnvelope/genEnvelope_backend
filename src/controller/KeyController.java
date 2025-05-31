package controller;

import domain.KeyPairInfo;
import service.KeyService;

public class KeyController {
    private final KeyService keyService = new KeyService();

    public boolean handleGenerateKeyAndSave(String publicPath, String privatePath) {
        try {
            KeyPairInfo keyPair = keyService.createKeyPair();
            keyService.saveKeys(keyPair, publicPath, privatePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
