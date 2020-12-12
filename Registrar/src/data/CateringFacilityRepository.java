package data;

import model.CateringFacility;

import java.util.ArrayList;
import java.util.List;

// NON PERSISTENT
public class CateringFacilityRepository {

    private static CateringFacilityRepository instance;
    private List<CateringFacility> cateringFacilityList;

    private CateringFacilityRepository() {
        cateringFacilityList = new ArrayList<>();
    }

    public static synchronized CateringFacilityRepository getInstance() {
        if (instance == null) {
            instance = new CateringFacilityRepository();
        }
        return instance;
    }


    public void addCateringFacility(CateringFacility cateringFacility) {
        if (!cateringFacilityList.contains(cateringFacility)) {
            cateringFacilityList.add(cateringFacility);
        }

    }

    public List<CateringFacility> getCateringFacilityList() {
        return cateringFacilityList;
    }


    public CateringFacility getCateringFacility(long businessNumber, String location) {
        for (CateringFacility cf : cateringFacilityList) {
            if (cf.getBusinessNumber() == businessNumber && cf.getLocation() == location) {
                return cf;
            }
        }
        return null;
    }
}
