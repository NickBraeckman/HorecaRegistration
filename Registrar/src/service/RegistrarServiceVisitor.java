package service;

import com.sun.istack.internal.Nullable;
import data.VisitorRepository;
import util.TokenGenerator;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;

//LOCAL
public class RegistrarServiceVisitor extends UnicastRemoteObject implements RegistrarServiceVisitorInterface {

    private static final Logger logger = Logger.getLogger(RegistrarServiceVisitor.class.getName());

    public RegistrarServiceVisitor(int port) throws RemoteException {
        super(port);
    }

    /**
     * 1.2 user enrolment
     * @param phoneNumber send by Visitor
     * @param visitorService callback to Visitor
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean authenticate(String phoneNumber, VisitorServiceRegistrarInterface visitorService) throws RemoteException {
        try {
            if (VisitorRepository.getInstance().isVisitorRegistered(phoneNumber)) try {
                TokenBatch tokenBatch = TokenGenerator.generateDailyTokens(phoneNumber);
                info("Visitor: " + phoneNumber +" is online");
                if (tokenBatch != null) {
                    visitorService.saveDailyTokens(tokenBatch);
                }

            } catch (SignatureException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                e.printStackTrace();
                return false;
            }
            else {
                TokenBatch tokenBatch = TokenGenerator.generateInitTokens(phoneNumber);
                visitorService.saveDailyTokens(tokenBatch);
                info("Visitor: " + phoneNumber +" is registered");
            }
        } catch (IOException | SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public TokenBatch receiveDailyTokens(String phoneNumber) throws RemoteException {
        return null;
    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
}
