package service;

import util.EnvelopeUtils;
import util.KeyManager;

import java.security.KeyPair;

public class EnvelopeService {

    public void generateEnvelope(String resultPath, String markPath, String receiverName, String zipPath, boolean isFake) {
        try {
            // 검사기관 키쌍 로드
            KeyPair labKeyPair = KeyManager.loadOrGenerateKeyPair("data/labPrivate", "data/labPublic");

            // 수신자 공개키 경로
            String receiverPublicKeyPath = "data/" + receiverName + "public";

            // 인증서 경로
            String certificatePath = "data/certificate.txt";

            // 전자봉투 생성
            EnvelopeUtils.createSecureEnvelope(
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
