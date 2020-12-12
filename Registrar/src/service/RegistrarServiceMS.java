package service;

import com.sun.istack.internal.Nullable;
import data.PseudonymRepository;
import data.VisitorRepository;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistrarServiceMS extends UnicastRemoteObject implements RegistrarServiceMSInterface {

    private static final Logger logger = Logger.getLogger(RegistrarServiceVisitor.class.getName());

    public RegistrarServiceMS(int port) throws RemoteException {
        super(port);
    }

    /**
     * 3.2 submit logs -> control location data of infected user by taking the hash on the daily pseudonyms
     * and comparing it with the log
     * @param date
     * @return
     * @throws RemoteException
     */
    @Override
    public List<String> requestPseudonyms(LocalDate date) throws RemoteException {
        List<String> pseudonyms = new ArrayList<>();

        try {
            pseudonyms = PseudonymRepository.getInstance().getPseudonyms(date);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pseudonyms;
    }

    /**
     * 4.4 Forward unacked logs
     * @param tokenSigns
     * @throws RemoteException
     */
    @Override
    public void sendUninformedTokens(List<String> tokenSigns) throws RemoteException {
        Set<String> phoneNumbersToContact = new HashSet<>();

        for (String tokenSign : tokenSigns){
            String phoneNumber = VisitorRepository.getInstance().getVisitorByTokenSign(tokenSign);
            if (phoneNumber != null){
                phoneNumbersToContact.add(phoneNumber);
            }
        }

        for (String phoneNumber : phoneNumbersToContact){
            info("Contact: " + phoneNumber);
        }

    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
}
