package model;

import service.CFServiceInterface;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CateringFacility {

    private long businessNumber;
    private String password;
    private String location;
    private LocalDate registrationDate;
    private Map<String, String> monthlyPseudonyms;
    private CFServiceInterface cfService;

    public CateringFacility(long businessNumber, String location, String password, CFServiceInterface cfService) {
        this.businessNumber = businessNumber;
        this.location = location;
        this.cfService = cfService;
        this.registrationDate = LocalDate.now();
        this.password = password;
        this.monthlyPseudonyms = new HashMap<>();
    }

    public long getBusinessNumber() {
        return businessNumber;
    }

    public String getLocation() {
        return location;
    }

    public CFServiceInterface getCfService() {
        return cfService;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public String getPassword() {
        return password;
    }

    public void sendMonthlyPseudonyms(Map<String, String> pseudonyms) {

        // try to send pseudonyms to client
        try {
            cfService.saveMonthlyPseudonyms(pseudonyms);
        } catch (RemoteException e) {
            e.printStackTrace();

            // client is offline -> store it till client back online
            monthlyPseudonyms = pseudonyms;
        }
    }

    // send monthly pseudonyms that are stored because the client was not online
    public void sendOldMonthlyPseudonyms() {
        if (!monthlyPseudonyms.isEmpty()) {
            Map<String, String> pseudonyms = monthlyPseudonyms;
            monthlyPseudonyms = new HashMap<>();
            try {
                cfService.saveMonthlyPseudonyms(pseudonyms);
            } catch (RemoteException e){
                e.printStackTrace();
                monthlyPseudonyms = pseudonyms;
            }
        }
    }
}
