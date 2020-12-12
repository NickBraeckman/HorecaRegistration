package service;

import com.sun.istack.internal.Nullable;
import data.PseudonymRepository;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// LOCAL
public class CFService extends UnicastRemoteObject implements CFServiceInterface {

    private RegistrarServiceCFInterface registrarService;
    private static final Logger logger = Logger.getLogger(CFService.class.getName());

    public CFService(RegistrarServiceCFInterface registrarService) throws RemoteException {
        super();
        this.registrarService = registrarService;
    }

    @Override
    public void saveMonthlyPseudonyms(Map<String, String> pseudonyms) throws RemoteException {
        try {
            PseudonymRepository.getInstance().putAllPseudonyms(pseudonyms);
        } catch (IOException e) {
            e.printStackTrace();
            error("Could not store the pseudonyms");
        }
        info("Received monthly pseudonyms" + pseudonyms);

    }

    public boolean authenticate(long businessNumber, String location, String password) {

        boolean isAuthenticated = false;

        try {
            isAuthenticated = registrarService.authenticateCateringFacility(businessNumber, location, password,this);
        } catch (RemoteException e) {
            error("could not authenticate catering facility: ", e.getMessage());
            e.printStackTrace();
        }
        return isAuthenticated;
    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
}
