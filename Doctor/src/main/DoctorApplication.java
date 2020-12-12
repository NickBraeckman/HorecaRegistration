package main;

import util.Config;

import java.util.Scanner;

public class DoctorApplication {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Directory Name Visitor:");
        String dirName = sc.nextLine();
        Config.VISITOR_DIR_NAME = dirName;

        DoctorController controller = new DoctorController();
        controller.start();
    }
}
