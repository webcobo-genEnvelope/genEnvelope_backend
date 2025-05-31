package util;

import javax.crypto.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EnvelopeUtils {
    public static void createSecureEnvelope(String resultPath, String markPath, String zipPath,
                                            String receiverPublicKeyPath, KeyPair labKeyPair,
                                            String certificatePath, boolean isFake) {
        try {
            SecretKey aesKey = KeyGenerator.getInstance("AES").generateKey();
            PublicKey receiverKey = KeyManager.loadPublicKey(receiverPublicKeyPath);

            // 1. 결과 파일 서명
            byte[] resultBytes = Files.readAllBytes(Paths.get(resultPath));
            byte[] resultHash = CryptoUtils.hash(resultBytes);
            byte[] resultSig = CryptoUtils.sign(resultHash, labKeyPair.getPrivate());

            // 2. 마크 파일 처리
            byte[] markBytes = Files.readAllBytes(Paths.get(markPath));
            byte[] markSig;
            if (isFake) {
                markSig = CryptoUtils.sign("wrong_fake_data".getBytes(), labKeyPair.getPrivate());
            } else {
                byte[] markHash = CryptoUtils.hash(markBytes);
                markSig = CryptoUtils.sign(markHash, labKeyPair.getPrivate());
            }

            // 인증서 파일
            byte[] certBytes = Files.readAllBytes(Paths.get(certificatePath));

            // 3. 임시 폴더 생성 및 파일 저장
            File tempDir = new File("data/temp_content");
            tempDir.mkdir();
            Files.write(Paths.get("data/temp_content/result.txt"), resultBytes);
            Files.write(Paths.get("data/temp_content/result.sig"), resultSig);
            Files.write(Paths.get("data/temp_content/certified_mark.png"), markBytes); // 이름 고정!
            Files.write(Paths.get("data/temp_content/mark.sig"), markSig);
            Files.write(Paths.get("data/temp_content/certificate.txt"), certBytes);

            // 4. AES로 압축 → 암호화
            FileOutputStream cipherOut = new FileOutputStream("data/cipher_payload.dat");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (File f : tempDir.listFiles()) {
                    zos.putNextEntry(new ZipEntry(f.getName()));
                    zos.write(Files.readAllBytes(f.toPath()));
                    zos.closeEntry();
                }
            }

            byte[] encryptedPayload = cipher.doFinal(baos.toByteArray());
            cipherOut.write(encryptedPayload);
            cipherOut.close();

            // 5. AES 키 RSA로 암호화
            byte[] encryptedAesKey = CryptoUtils.encryptKeyWithRSA(aesKey, receiverKey);

            // 6. 최종 봉투 생성
            try (FileOutputStream fos = new FileOutputStream(zipPath);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {
                writeToZip(zos, "암호문.dat", encryptedPayload);
                writeToZip(zos, "envelope.key", encryptedAesKey);
            }

            // 7. 임시 폴더 정리
            for (File f : tempDir.listFiles()) f.delete();
            tempDir.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeToZip(ZipOutputStream zos, String name, byte[] data) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(data);
        zos.closeEntry();
    }
}
