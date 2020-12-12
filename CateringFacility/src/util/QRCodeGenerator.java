package util;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import data.CSVDataManager;
import data.PseudonymRepository;

import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class QRCodeGenerator {

    public static QRCode generateDailyQRCode(long businessNumber) throws NoSuchAlgorithmException, IOException {
        // generates new daily code QR_CF_day_i
        // Ri is a random number
        // nym_CF_day_i is the pseudonym
        // QR_CF_day_i = [Ri, CF, H(Ri,nym_CF_day_i)]
        // H is a cryptographic hashing function

        MessageDigest digester = MessageDigest.getInstance("SHA-256");
        SecureRandom random = new SecureRandom();

        // Ri
        if (PseudonymRepository.getInstance().getDailyPseudonym() != null) {
            String pseudonym = PseudonymRepository.getInstance().getDailyPseudonym();
            byte [] pseudonymBytes = pseudonym.getBytes();
            byte[] ri = new byte[20];
            byte[] hash;

            // creating random bytes for ri
            random.nextBytes(ri);

            // H(Ri, nym_CF_day_i)
            ByteOutputStream bos = new ByteOutputStream();
            byte[] output;

            try (DataOutputStream dos = new DataOutputStream(bos)) {
                dos.write(ri);
                dos.write(pseudonymBytes);
                dos.flush();
                hash = digester.digest(bos.getBytes());

                bos.close();
            }

            return new QRCode(ri, businessNumber, hash);
        } else throw new RuntimeException("Could not retrieve the pseudonyms: database not yet persistent");
    }

}
