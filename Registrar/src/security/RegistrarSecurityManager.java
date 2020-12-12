package security;

import data.CSVDataManager;
import service.Token;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.*;
import java.time.LocalDate;
import java.util.Base64;

// Singleton instance that manages the master secret key and the key generation
public class RegistrarSecurityManager {

    private static RegistrarSecurityManager instance;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private RegistrarSecurityManager() {
        initKeyPair();
    }

    public static synchronized RegistrarSecurityManager getInstance() {
        if (instance == null) {
            instance = new RegistrarSecurityManager();
        }
        return instance;
    }

    /**
     * Generate private and public key pair at startup
     */
    private void initKeyPair() {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair rsaPair = keyGen.generateKeyPair();
            publicKey = rsaPair.getPublic();
            CSVDataManager.getInstance().writePublicKeyToCsv(CSVDataManager.CSVFile.PUBLIC_KEYS_REGISTRAR,publicKey);
            privateKey = rsaPair.getPrivate();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  generate each day_i a new pseudonym nym_CF_day_i
     *  H is a cryptographic hashing function
     *  nym_CF_day_i = H(sKeyCF, locationCF, day_i)
     * @param businessNumber
     * @param location
     * @param day
     * @return
     * @throws Exception
     */
    public String generatePseudonym(long businessNumber, String location, LocalDate day) throws Exception {

        MessageDigest digester = MessageDigest.getInstance("SHA-256");
        byte[] secretKey = generateSecretKey(businessNumber, day);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        dos.write(location.getBytes());
        dos.write(secretKey);
        dos.write(day.toString().getBytes());
        dos.flush();

        byte[] output = digester.digest(bos.toByteArray());

        dos.close();
        bos.close();

        String outputString = Base64.getEncoder().encodeToString(output);

        return outputString;

    }

    /**
     * generate each day_i a new secret key s_CF_day_i
     * KDF is a secure key derivation function
     * CF is the business number
     * s_CF_day_i = KDF( sKeyRegistrar, CF, day_i)
     * @param businessNumber cf
     * @param day day_i
     * @return encoded secret key
     * @throws Exception
     */
    private byte[] generateSecretKey(long businessNumber, LocalDate day) throws Exception {

        int iterations = 250000;
        int keyLength = 256;
        byte[] salt = getNewSalt();

        // hard coded master secret key
        char[] chars = (String.valueOf(businessNumber) + String.valueOf("mastersecretkey1234567891011121314151617181920") + String.valueOf(day)).toCharArray();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, keyLength);

        // PBKDF2 -> password based key derivation function
        // HMAC -> pseudo random function
        // if the password is longer than the block size of HMAC hash function,
        // the password is pre-hashed (SHA256) into a digest, then the digest is used instead of the password

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] key = keyFactory.generateSecret(spec).getEncoded();
        return key;
    }

    private byte[] getNewSalt() throws Exception {

        // never use Random
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        // NIST recommends minimum 4 bytes. We use 8.
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt).getBytes();
    }


    /**
     * signing of visitor token
     * @param date
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws SignatureException
     */
    public Token generateToken(LocalDate date) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[10];
        random.nextBytes(bytes);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        dos.write(bytes);
        dos.writeBytes(date.toString());
        dos.flush();
        byte[] output = bos.toByteArray();

        // create and initialize signature object
        Signature signEngine = Signature.getInstance("SHA256withRSA");
        signEngine.initSign(privateKey);

        // sign data
        signEngine.update(output);
        byte[] signature = signEngine.sign();

        String data = Base64.getEncoder().encodeToString(output);
        String sign = Base64.getEncoder().encodeToString(signature);

        return new Token(sign,data);
    }

}
