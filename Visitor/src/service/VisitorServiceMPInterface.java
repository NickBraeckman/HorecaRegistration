package service;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VisitorServiceMPInterface extends Remote {

    void receiveAcknowledgment(Acknowledgement acknowledgement) throws RemoteException;
}
