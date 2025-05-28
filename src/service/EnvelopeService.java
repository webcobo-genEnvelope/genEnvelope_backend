package service;

import domain.GeneticEnvelope;
import repository.EnvelopeRepository;
import util.EnvelopeUtils;
import util.KeyManager;

import java.security.KeyPair;

public class EnvelopeService {
    private final EnvelopeRepository repository = new EnvelopeRepository();

    public void generateEnvelope(String resultPath, String markPath) {
        String zipPath = "data/envelope.zip";

        KeyPair rsaKeyPair = KeyManager.loadOrGenerateKeyPair();

        //암호화, 서명, 봉투 생성
        EnvelopeUtils.createEnvelopeWithCrypto(resultPath, markPath, zipPath, rsaKeyPair);

        GeneticEnvelope envelope = new GeneticEnvelope(resultPath, markPath, zipPath);
        repository.save(envelope);
    }
}