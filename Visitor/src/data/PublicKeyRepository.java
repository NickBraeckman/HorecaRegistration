package data;

import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class PublicKeyRepository {

    private final CSVDataManager dataManager;

    public PublicKeyRepository(CSVDataManager dataManager) {
        this.dataManager = dataManager;
    }

    public PublicKey getRegistrarPublicKey(CSVDataManager.CSVFile csvFile) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {

        String publicKey = null;

        Iterable<CSVRecord> records = dataManager.getCSVRecords(csvFile);
        for (CSVRecord record : records) {
            publicKey = record.get("key");
        }
        return loadPublicKey(publicKey);
    }

    private static PublicKey loadPublicKey(String keyValue) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] data = Base64.getDecoder().decode(keyValue.getBytes());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePublic(spec);
    }
}
