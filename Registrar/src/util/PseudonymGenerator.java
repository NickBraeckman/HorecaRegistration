package util;

import data.PseudonymRepository;
import model.CateringFacility;
import security.RegistrarSecurityManager;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PseudonymGenerator {

    public static Map<String, String> generatePseudonyms(CateringFacility cateringFacility, LocalDate day) throws Exception {

        long businessNumber = cateringFacility.getBusinessNumber();
        String location = cateringFacility.getLocation();
        Map<String, String> pseudonyms = new HashMap<>();

        pseudonyms.put(day.toString(), RegistrarSecurityManager.getInstance().generatePseudonym(businessNumber, location, day));

        if (!pseudonyms.isEmpty()) {
            pseudonyms.forEach((key, value) -> {
                try {
                    PseudonymRepository.getInstance().putPseudonym(key, value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        return pseudonyms;
    }

    public static Map<String, String> generateInitialPseudonyms(CateringFacility cateringFacility) throws Exception {

        LocalDate cateringFacilityRegistrationDate = cateringFacility.getRegistrationDate();
        int start = cateringFacilityRegistrationDate.getDayOfMonth();
        int length = cateringFacility.getRegistrationDate().lengthOfMonth();
        int end = length - start;
        Map<String, String> pseudonyms = new HashMap<>();

        String location = cateringFacility.getLocation();
        long businessNumber = cateringFacility.getBusinessNumber();

        LocalDate temp = cateringFacilityRegistrationDate;
        for (int i = 0; i < end; i++) {
            try {

                pseudonyms.put(temp.plusDays(i).toString(), RegistrarSecurityManager.getInstance().generatePseudonym(businessNumber, location, temp));
            } catch (NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            }
        }

        if (!pseudonyms.isEmpty()) {
            pseudonyms.forEach((key, value) -> {
                try {
                    PseudonymRepository.getInstance().putPseudonym(key, value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        return pseudonyms;
    }
}
