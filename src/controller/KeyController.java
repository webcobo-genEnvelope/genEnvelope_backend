package controller;

import domain.KeyPairInfo;
import service.KeyService;

public class KeyController {

    private final KeyService keyService = new KeyService();

    // 키 생성 및 저장을 한 번에 수행
    public boolean handleGenerateKeyAndSave(String publicPath, String privatePath) {
        try {
            KeyPairInfo keyPair = keyService.createKeyPair();
            keyService.saveKeys(keyPair, publicPath, privatePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // 디버깅용
            return false;
        }
    }
}
