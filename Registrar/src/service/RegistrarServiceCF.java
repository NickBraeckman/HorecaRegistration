package service;

import com.sun.istack.internal.Nullable;
import data.CateringFacilityRepository;
import main.RegistrarServerApplication;
import model.CateringFacility;
import util.PseudonymGenerator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// LOCAL
public class RegistrarServiceCF extends UnicastRemoteObject implements RegistrarServiceCFInterface {

    private static final Logger logger = Logger.getLogger(RegistrarServerApplication.class.getName());
    private CateringFacilityRepository repository = CateringFacilityRepository.getInstance();

    public RegistrarServiceCF(int port) throws RemoteException {
        super(port);
    }

    @Override
    public boolean authenticateCateringFacility(long businessNumber, String location, String password, CFServiceInterface cfService) throws RemoteException {

        // registering
        if (repository.getCateringFacilityList().isEmpty()) {
            registerCateringFacility(businessNumber, location, password, cfService);
            return true;

        } else {

            for (CateringFacility cf : repository.getCateringFacilityList()) {

                // check if catering facility is already registered
                if (cf.getBusinessNumber() != businessNumber) {
                    registerCateringFacility(businessNumber, location, password, cfService);
                    return true;

                    // catering facility is online
                } else if (cf.getBusinessNumber() == businessNumber && cf.getLocation().equals(location)) {

                    // check password
                    if (cf.getPassword().equals(password)) {
                        cf.sendOldMonthlyPseudonyms();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void registerCateringFacility(long businessNumber, String location, String password, CFServiceInterface cfService) {

        CateringFacility cateringFacility = new CateringFacility(businessNumber, location, password, cfService);
        info("CateringFacility: " + businessNumber + " is authenticated");

        // send initial pseudonyms
        try {
            Map<String, String> pseudonyms = PseudonymGenerator.generateInitialPseudonyms(cateringFacility);
            cfService.saveMonthlyPseudonyms(pseudonyms);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // add catering facility to list of catering facilities
        repository.addCateringFacility(cateringFacility);
    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }

}
