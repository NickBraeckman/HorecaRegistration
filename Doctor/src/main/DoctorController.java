package main;

import com.sun.istack.internal.Nullable;
import data.CapsuleRepository;
import security.DoctorSecurityManager;
import service.InfectedCapsule;
import service.MatchingServiceInterface;
import util.Constants;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoctorController {

    private Registry registry;
    private MatchingServiceInterface matchingService;
    private long businessNumber;

    private boolean isAuthenticated;

    private static final Logger logger = Logger.getLogger(DoctorController.class.getName());

    public DoctorController() {
    }

    public void start() {

        try {
            registry = LocateRegistry.getRegistry(Constants.MATCHING_SERVER_NAME, Constants.MATCHING_PORT_NUMBER);
            matchingService = (MatchingServiceInterface) registry.lookup(MatchingServiceInterface.SERVICE_NAME);

            sendInfectedCapsules(LocalDateTime.now().minusSeconds(Constants.INCUBATION_TIME), LocalDateTime.now());

        } catch (RemoteException e) {
            error("DoctorApplication could not connect");
            e.printStackTrace();
            System.exit(0);
        } catch (NotBoundException e) {
            error("DoctorApplication could not bind");
            e.printStackTrace();
            System.exit(0);
        }

    }

    public void sendInfectedCapsules(LocalDateTime from, LocalDateTime until) throws RemoteException {
        try {
            List<InfectedCapsule> infectedCapsuleList = CapsuleRepository.getInstance().getVisitorLog(from, until);
            infectedCapsuleList = DoctorSecurityManager.getInstance().signInfectedCapsules(infectedCapsuleList);
            Collections.shuffle(infectedCapsuleList);
            CapsuleRepository.getInstance().addInfectedCapsules(infectedCapsuleList);

            matchingService.sendInfectedCapsule(infectedCapsuleList);

        } catch (IOException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
}
