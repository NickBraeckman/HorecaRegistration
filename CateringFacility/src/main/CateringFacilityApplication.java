package main;

import util.Config;

import java.util.Scanner;

public class CateringFacilityApplication {

    public static void main(String[] args) {

        CateringFacilityController controller = new CateringFacilityController();
        long businessNumber = 0;
        String location = null;
        String password = null;
        boolean isAuthenticated = false;

        controller.start();

        Scanner scanner = new Scanner(System.in);

        while (!isAuthenticated) {
            System.out.println("Enter business number: ");
            businessNumber = Long.valueOf(scanner.nextLine());


            System.out.println("Enter location: ");
            location = String.valueOf(scanner.nextLine());

            System.out.println("Enter password: ");
            password = String.valueOf(scanner.nextLine());

            isAuthenticated = controller.authenticate(businessNumber, location, password);
        }

    }
}
