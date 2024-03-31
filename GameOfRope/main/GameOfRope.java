package main;

import sharedRegions.*;
import entities.*;
import commonInfra.Strategy;

import java.util.Scanner;

//import commonInfra.Strategy;

import java.io.File;

public class GameOfRope {
    Referee referee;

    public static void main(String[] args) {

        /**
         * Declaring Shared Regions
         */
        GeneralRepository repo;
        Playground playground;
        RefereeSite refereeSite;
        ContestantBench contestantBench;

        /**
         * Declaring Threads
         */
        Referee referee;
        Coach[] coach = new Coach[2];
        Contestant[][] contestants = new Contestant[2][SimulParse.CONTESTANT_PER_TEAM];

        /**
         * Strength that is randomly generated for each contestant
         */
        int[][] contestantStrength = new int[2][SimulParse.CONTESTANT_PER_TEAM];

        /**
         * Choosing which file to print the output
         */
        String fileName = "log";
        char option;
        boolean success = false;

        Scanner sc = new Scanner(System.in);

        System.out.println("Game of the Rope - Description of the internal state");
        if (fileName.length() == 0) {
            do {
                System.out.println("Enter the name of the file to save the log: ");
                fileName = sc.nextLine();
                File file = new File(fileName);
                if (file.exists()) {
                    do {
                        System.out.println("File already exists. Do you want to overwrite it? (y/n)");
                        String answer = sc.nextLine();
                        try {
                            option = answer.charAt(0);
                        } catch (StringIndexOutOfBoundsException e) {
                            option = 'y';
                        }
                    } while (option != 'y' && option != 'n');

                    if (option == 'n') {
                        success = false;
                    } else {
                        success = true;
                    }
                } else {
                    success = true;
                }
            } while (!success);

            sc.close();
        }

        /**
         * Generates the strength of each Contestant
         */
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < SimulParse.CONTESTANT_PER_TEAM; j++) {
                contestantStrength[i][j] = (int) (5 * Math.random() + 6);
            }
        }

        /**
         * Instanciate each shared region
         */
        repo = new GeneralRepository(fileName, contestantStrength);
        refereeSite = new RefereeSite(repo);
        playground = new Playground(repo);
        contestantBench = new ContestantBench(repo);

        /**
         * Instanciate each thread
         */
        referee = new Referee(playground, refereeSite, contestantBench);

        for (int i = 0; i < 2; i++) {
            coach[i] = new Coach(i, contestantBench, playground, refereeSite, Strategy.StrategyType.STRONGEST);
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < SimulParse.CONTESTANT_PER_TEAM; j++) {
                contestants[i][j] = new Contestant(i, j, contestantStrength[i][j], playground, refereeSite,
                        contestantBench);
            }
        }

        /**
         * Starting the lifecycle of each Thread/Entity
         */
        referee.start();

        for (Coach c : coach) {
            c.start();
        }

        for (int i = 0; i < 2; i++) {
            for (Contestant c : contestants[i]) {
                c.start();
            }
        }

        /**
         * Joining/Ending the lifecycle of each Thread/Entity
         */
        try {
            referee.join();
        } catch (InterruptedException e) {
        }
        System.out.println("Referee() has ended");

        for (int i = 0; i < 2; i++) {
            try {
                coach[i].join();
            } catch (InterruptedException e) {
            }
            System.out.println("Coach(" + i + ") has ended.");
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < SimulParse.CONTESTANT_PER_TEAM; j++) {
                try {
                    contestants[i][j].join();
                } catch (InterruptedException e) {
                }
                System.out.println("Contestant(T" + i + "," + j + ") has ended.");
            }
        }

        repo.legend();
    }
}
