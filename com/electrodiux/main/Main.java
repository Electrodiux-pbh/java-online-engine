package com.electrodiux.main;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("server")) {
                MainServer.main(args);
                return;
            }
        }
        MainClient.main(args);
    }

}
