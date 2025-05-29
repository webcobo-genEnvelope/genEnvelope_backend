package service;

import domain.GeneticEnvelope;
import repository.EnvelopeRepository;
import util.EnvelopeUtils;
import util.KeyManager;

import java.security.KeyPair;

public class EnvelopeService {
    private final EnvelopeRepository repository = new EnvelopeRepository();

    public void generateEnvelope(String resultPath, String markPath, String zipPath) {
        KeyPair labKeyPair = KeyManager.loadOrGenerateLabKeyPair();
        KeyManager.UserPublicKey userKey = KeyManager.loadUserPublicKey();

        EnvelopeUtils.createEnvelopeWithCrypto(
                resultPath, markPath, zipPath,
                labKeyPair.getPrivate(), userKey.getPublicKey()
        );

        GeneticEnvelope envelope = new GeneticEnvelope(resultPath, markPath, zipPath);
        repository.save(envelope);
    }
}