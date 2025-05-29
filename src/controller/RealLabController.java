package controller;

import service.EnvelopeService;

public class RealLabController {
    private final EnvelopeService service = new EnvelopeService();

    public void createEnvelope() {
        String resultPath = "data/result.txt";
        String markPath = "data/certified_mark.png";
        String zipPath = "data/envelope.zip";
        service.generateEnvelope(resultPath, markPath, zipPath);
    }
}