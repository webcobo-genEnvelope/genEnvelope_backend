package service;

import util.EnvelopeUtils;
import util.KeyManager;

import java.security.KeyPair;

public class EnvelopeService {

    public void genEnvelope(String resultPath, String markPath, String receiverName, String zipPath, boolean isFake) {
        try {
            KeyPair labKeyPair = KeyManager.loadOrGenKeyPair("data/labPrivate", "data/labPublic");

            String receiverPublicKeyPath = "data/" + receiverName + "public";

            String certificatePath = "data/certificate.txt";

            EnvelopeUtils.createSecEnv(
                    resultPath,
                    markPath,
                    zipPath,
                    receiverPublicKeyPath,
                    labKeyPair,
                    certificatePath,
                    isFake
            );

            System.out.println("📦 전자봉투 저장 완료: " + zipPath);

        } catch (Exception e) {
            System.err.println("❌ 전자봉투 생성 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
