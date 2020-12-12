package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VisitorServiceRegistrarInterface extends Remote {

    void saveDailyTokens(TokenBatch tokenBatch) throws RemoteException;
}
