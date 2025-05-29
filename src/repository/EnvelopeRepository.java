package repository;

import domain.GeneticEnvelope;

public class EnvelopeRepository {
    public void save(GeneticEnvelope envelope) {
        System.out.println("💾 전자봉투 저장됨: " + envelope.getEnvelopeZipPath());
    }
}