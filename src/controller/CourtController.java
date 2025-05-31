package controller;

import service.EnvelopeService;

public class CourtController {
    private final EnvelopeService service = new EnvelopeService();

    // ✅ isFake 파라미터 포함하도록 수정
    public void create(String receiver, String resultPath, String markPath, String zipPath, boolean isFake) {
        service.generateEnvelope(resultPath, markPath, receiver, zipPath, isFake);
    }
}
