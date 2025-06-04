package util;

import domain.VerifiResult;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.logging.Logger;
import java.util.logging.Level;

public class EnvelopeVerifier {

    private static final Logger LOGGER = Logger.getLogger(EnvelopeVerifier.class.getName());

    private static final String CIPHER_FILE_NAME = "encrypted_data.dat";
    private static final String KEY_FILE_NAME = "envelope.key";

    public static VerifiResult verify(String zipPath, String privateKeyPath) {
        try {
            PrivateKey privateKey = KeyManager.loadPrivateKey(privateKeyPath);

            byte[] encryptedAesKey = null;
            byte[] encryptedPayload = null;

            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    switch (entry.getName()) {
                        case KEY_FILE_NAME -> encryptedAesKey = zis.readAllBytes();
                        case CIPHER_FILE_NAME -> encryptedPayload = zis.readAllBytes();
                    }
                }
            }

            if (encryptedAesKey == null || encryptedPayload == null) {
                throw new EnvelopeException("전자봉투 형식 오류: 필수 파일 누락");
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
            if (!sig.verify(resultSig)) {
                resultTampered = true;
                LOGGER.warning("결과 파일 서명 검증 실패");
            }

            sig.initVerify(pubKey);
            sig.update(CryptoUtils.hash(markBytes));
            if (!sig.verify(markSig)) {
                markTampered = true;
                LOGGER.warning("인증 마크 서명 검증 실패");
            }

            String resultText = new String(resultBytes);
            String status;
            if (resultTampered && markTampered) {
                status = "결과와 인증 마크 모두 위조됨";
            } else if (resultTampered) {
                status = "결과 파일 위조 - 진본 아님";
            } else if (markTampered) {
                status = "인증 마크 위조 - 진본 아님";
            } else {
                status = "진본 확인됨";
            }

            return new VerifiResult(status, resultText);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "전자봉투 검증 중 오류 발생", e);
            throw new EnvelopeException("검증 실패", e);
        }
    }
}
