package security;

import data.CSVDataManager;
import data.PublicKeyRepository;
import service.Acknowledgement;
import service.Capsule;
import service.Token;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;


public class MPSecurityManager {

    private static MPSecurityManager instance;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private MPSecurityManager() {
        initKeyPair();
    }

    public static synchronized MPSecurityManager getInstance() {
        if (instance == null) {
            instance = new MPSecurityManager();
        }
        return instance;
    }

    private void initKeyPair() {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048,new SecureRandom());
            KeyPair rsaPair = keyGen.generateKeyPair();
            publicKey = rsaPair.getPublic();
            CSVDataManager.getInstance().writePublicKeyToCsv(CSVDataManager.CSVFile.PUBLIC_KEYS_MP,publicKey);
            privateKey = rsaPair.getPrivate();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean verifySignToken(Token token, LocalDate date) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {

        boolean isValid = false;

        // The signature algorithm with SHA-* and the RSA encryption algorithm as defined in the OSI Interoperability Workshop, using the padding conventions described in PKCS #1
        Signature signEngine = Signature.getInstance("SHA256withRSA");
        PublicKey key = PublicKeyRepository.getInstance().getRegistrarPublicKey(CSVDataManager.CSVFile.PUBLIC_KEYS_REGISTRAR);

        byte[] sign = Base64.getDecoder().decode((token.getSign()).getBytes());
        byte[] data = Base64.getDecoder().decode(token.getData().getBytes());
        byte[] dateBytes = Arrays.copyOfRange(data,10,data.length);

        LocalDate tokenDate = LocalDate.parse(new String(dateBytes));

        // verify signature
        signEngine.initVerify(key);
        signEngine.update(data);

        // check the sign of the token
        isValid = signEngine.verify(sign);

        // check the date of the token
        if (!tokenDate.isEqual(date)){
            isValid = false;
        }

        return isValid;
    }

    public Acknowledgement generateAcknowledgement(Capsule capsule) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {

        byte[] output = capsule.getHash().getBytes();

        // The signature algorithm with SHA-* and the RSA encryption algorithm as defined in the OSI Interoperability Workshop, using the padding conventions described in PKCS #1.
        Signature signEngine = Signature.getInstance("SHA256withRSA");
        signEngine.initSign(privateKey);

        signEngine.update(output);
        byte[] signature = signEngine.sign();

        String data = Base64.getEncoder().encodeToString(output);
        String sign = Base64.getEncoder().encodeToString(signature);

        return new Acknowledgement(data,sign);
    }
}
