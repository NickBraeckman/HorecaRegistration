package main;

import java.util.Scanner;

public class RegistrarServerApplication {

    public static void main(String[] args) {
        RegistrarServer server = new RegistrarServer();
        server.start();
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()){
        if (sc.nextLine().equals("print")){
            server.printDatabase();
        }}
    }

}
