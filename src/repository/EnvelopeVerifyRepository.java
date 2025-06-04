package repository;

import domain.VerifiResult;

public class EnvelopeVerifyRepository {
    public void save(VerifiResult result) {
        System.out.println("[✔️] 진위 여부: " + result.getStatus());
        System.out.println("[📄] 결과 내용: " + result.getContent());
    }
}
