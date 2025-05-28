package util;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.crypto.*;

public class EnvelopeUtils {
    public static void createEnvelopeWithCrypto(String resultPath, String markPath, String zipPath, KeyPair rsaKeyPair) {
        try {
            //키 생성
            SecretKey aesKey = KeyGenerator.getInstance("AES").generateKey();
            byte[] encryptedResult = CryptoUtils.encryptFileWithAES(resultPath, aesKey);
            byte[] resultHash = CryptoUtils.hash(encryptedResult);
            byte[] resultSig = CryptoUtils.sign(resultHash, rsaKeyPair.getPrivate());

            byte[] markBytes = Files.readAllBytes(Paths.get(markPath));
            byte[] markHash = CryptoUtils.hash(markBytes);
            byte[] markSig = CryptoUtils.sign(markHash, rsaKeyPair.getPrivate());

            try (FileOutputStream fos = new FileOutputStream(zipPath);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                writeToZip(zos, "encrypted_result.txt", encryptedResult);
                writeToZip(zos, "certified_mark.png", markBytes);
                writeToZip(zos, "result.sig", resultSig);
                writeToZip(zos, "mark.sig", markSig);
            }

            System.out.println("O : 전자봉투 생성 완료 (암호화 + 서명 포함): " + zipPath);
        } catch (Exception e) {
            System.out.println("X : 전자봉투 생성 실패: " + e.getMessage());
        }
    }

    private static void writeToZip(ZipOutputStream zos, String fileName, byte[] data) throws IOException {
        ZipEntry entry = new ZipEntry(fileName);
        zos.putNextEntry(entry);
        zos.write(data);
        zos.closeEntry();
    }
}