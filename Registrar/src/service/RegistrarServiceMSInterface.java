package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

public interface RegistrarServiceMSInterface extends Remote {
    public static String SERVICE_NAME = "RegistrarServiceMSInterface";

    List<String> requestPseudonyms(LocalDate date) throws RemoteException;

    void sendUninformedTokens(List<String> tokenSigns) throws RemoteException;
}
