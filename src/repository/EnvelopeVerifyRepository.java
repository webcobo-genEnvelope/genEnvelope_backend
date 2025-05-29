package repository;

import domain.VerificationResult;

public class EnvelopeVerifyRepository {
    public void save(VerificationResult result) {
        System.out.println("[✔️] 진위 여부: " + result.getStatus());
        System.out.println("[📄] 결과 내용: " + result.getContent());
    }
}
