package service;

import data.CapsuleRepository;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// LOCAL
public class MatchingServiceVisitor extends UnicastRemoteObject implements MatchingServiceVisitorInterface {

    public MatchingServiceVisitor(int port) throws RemoteException {
        super(port);
    }

    /**
     * 4.1 request infected capsules
     * visitor request all the tuples that are marked critical
     * @return
     * @throws RemoteException
     */
    @Override
    public List<CriticalTuple> requestCriticalTuples() throws RemoteException {
        List<CriticalTuple> criticalTuples = new ArrayList<>();

        try {
            criticalTuples = CapsuleRepository.getInstance().getCriticalUninformedCapsules(LocalDate.now());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return criticalTuples;
    }
}
