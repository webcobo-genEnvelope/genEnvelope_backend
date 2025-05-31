package util;

import domain.VerificationResult;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EnvelopeVerifier {

    public static VerificationResult verify(String zipPath, String privateKeyPath) throws Exception {
        PrivateKey privateKey = KeyManager.loadPrivateKey(privateKeyPath);

        byte[] encryptedAesKey = null;
        byte[] encryptedPayload = null;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                switch (entry.getName()) {
                    case "envelope.key" -> encryptedAesKey = zis.readAllBytes();
                    case "암호문.dat" -> encryptedPayload = zis.readAllBytes();  // 한글 파일명 확인
                }
            }
        }

        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decrypted = aesCipher.doFinal(encryptedPayload);

        File tempZip = new File("data/decrypted_payload.zip");
        Files.write(tempZip.toPath(), decrypted);

        String resultText = "";
        String status = "진본 확인됨";

        byte[] resultSig = null, markSig = null, resultBytes = null, markBytes = null, certBytes = null;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(tempZip))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                byte[] data = zis.readAllBytes();
                switch (entry.getName()) {
                    case "result.txt" -> resultBytes = data;
                    case "result.sig" -> resultSig = data;
                    case "certified_mark.png", "fake_mark.png", "certified_mark_fake.png" -> markBytes = data;
                    case "mark.sig" -> markSig = data;
                    case "certificate.txt" -> certBytes = data;
                }
            }
        }

        PublicKey pubKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(certBytes));

        Signature sig = Signature.getInstance("SHA256withRSA");

        boolean resultTampered = false;
        boolean markTampered = false;

        sig.initVerify(pubKey);
        sig.update(CryptoUtils.hash(resultBytes));
        if (!sig.verify(resultSig)) resultTampered = true;

        sig.initVerify(pubKey);
        sig.update(CryptoUtils.hash(markBytes));
        if (!sig.verify(markSig)) markTampered = true;

        if (resultTampered && markTampered) {
            status = "결과와 인증 마크 모두 위조됨";
        } else if (resultTampered) {
            status = "결과 파일 위조 - 진본 아님";
        } else if (markTampered) {
            status = "인증 마크 위조 - 진본 아님";
        }

        resultText = new String(resultBytes);
        return new VerificationResult(status, resultText);
    }
}
