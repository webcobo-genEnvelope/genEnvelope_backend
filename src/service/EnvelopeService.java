package service;

import domain.GeneticEnvelope;
import repository.EnvelopeRepository;
import util.EnvelopeUtils;
import util.KeyManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;

public class EnvelopeService {
    private final EnvelopeRepository repository = new EnvelopeRepository();

    public void generateEnvelope(String resultPath, String markPath, String receiverName) {
        String zipPath = "data/envelope.zip";
        String receiverPublicKeyPath = "data/" + receiverName + "public";
        String certificatePath = "data/certificate.txt";

        KeyPair labKeyPair = KeyManager.loadOrGenerateKeyPair("data/labPrivate", "data/labPublic");

        // 인증서 자동 생성
        try {
            File certFile = new File(certificatePath);
            if (!certFile.exists()) {
                Files.write(Paths.get(certificatePath), labKeyPair.getPublic().getEncoded());
                System.out.println("✅ 검사기관 인증서 자동 생성됨: " + certificatePath);
            }
        } catch (Exception e) {
            throw new RuntimeException("인증서 생성 실패: " + e.getMessage());
        }

        String markFullPath = "data/" + markPath;
        EnvelopeUtils.createSecureEnvelope(resultPath, markFullPath, zipPath, receiverPublicKeyPath, labKeyPair, certificatePath);

        GeneticEnvelope envelope = new GeneticEnvelope(resultPath, markFullPath, zipPath);
        repository.save(envelope);
    }
}