package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

//LOCAL
public interface CFServiceInterface extends Remote {
    void saveMonthlyPseudonyms(Map<String, String> pseudonyms) throws RemoteException;
}
