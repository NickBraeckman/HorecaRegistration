package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VisitorServiceMPInterface extends Remote{

    void receiveAcknowledgment(Acknowledgement acknowledgement) throws RemoteException;
}
