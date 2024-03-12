package fundamentals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

class Contestant {
    String name;
    int totalPoints;

    public Contestant(String name) {
        this.name = name;
        this.totalPoints = 0;
    }
}

class Region {
    String name;
    HashSet<Contestant> contestants;

    public Region(String name) {
        this.name = name;
        contestants = new HashSet<>();
    }
}

public class ElectionSystem {
    HashMap<Character, Contestant> contestantsMap;
    HashMap<String, Region> regionsMap;
    HashMap<String, Integer> invalidVotesMap;

    public ElectionSystem() {
        contestantsMap = new HashMap<>();
        regionsMap = new HashMap<>();
        invalidVotesMap = new HashMap<>();
    }

    public void parseInput(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()) {
            String[] regionData = scanner.nextLine().split("/");
            if(regionData.length>1) {
            String regionName = regionData[0];
            
            Region region = new Region(regionName);
            regionsMap.put(regionName, region);

            for (char c : regionData[1].toCharArray()) {
                Contestant contestant = contestantsMap.get(c);
                if (contestant == null) {
                    contestant = new Contestant(String.valueOf(c));
                    contestantsMap.put(c, contestant);
                }
                region.contestants.add(contestant);
            }
            }
        }
        scanner.close();
    }

    public void countVotes(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filename));
        Region currentRegion = null;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.equals("//")) {
                currentRegion = regionsMap.get(scanner.nextLine());
                continue;
            } else if (line.equals("&&")) {
                break;
            }

            if (currentRegion != null) {
                String[] voteData = line.split(" ");
                String voterID = voteData[0];
                HashSet<Contestant> regionContestants = currentRegion.contestants;
                if (regionContestants.stream().anyMatch(c -> c.name.equals(String.valueOf(voterID)))) {
                    invalidVotesMap.put(currentRegion.name, invalidVotesMap.getOrDefault(currentRegion.name, 0) + 1);
                    continue;
                }

                if (voteData.length < 2 || voteData.length > 4) {
                    invalidVotesMap.put(currentRegion.name, invalidVotesMap.getOrDefault(currentRegion.name, 0) + 1);
                    continue;
                }

                int points = 3;
                for (int i = 1; i < voteData.length; i++) {
                    Contestant contestant = contestantsMap.get(voteData[i].charAt(0));
                    if (contestant != null && regionContestants.contains(contestant)) {
                        contestant.totalPoints += points;
                        points--;
                    } else {
                        invalidVotesMap.put(currentRegion.name, invalidVotesMap.getOrDefault(currentRegion.name, 0) + 1);
                        break;
                    }
                }
            }
        }
        scanner.close();
    }

    public void displayResults() {
        Contestant chiefOfficer = contestantsMap.values().stream().max((c1, c2) -> c1.totalPoints - c2.totalPoints).get();
        System.out.println("Chief Officer: " + chiefOfficer.name + " (" + chiefOfficer.totalPoints + " points)");

        for (Region region : regionsMap.values()) {
            Contestant regionalHead = region.contestants.stream().max((c1, c2) -> c1.totalPoints - c2.totalPoints).get();
            System.out.println("Region " + region.name + ": " + regionalHead.name + " (" + regionalHead.totalPoints + " points)");
        }

        System.out.println("Invalid Votes:");
        for (String region : invalidVotesMap.keySet()) {
            System.out.println("Region " + region + ": " + invalidVotesMap.get(region));
        }
    }

    public static void main(String[] args) {
        ElectionSystem electionSystem = new ElectionSystem();
        try {
            electionSystem.parseInput("C:\\Users\\jaydu\\eclipse-workspace\\fundamentals\\src\\fundamentals\\voting.dat");
            electionSystem.countVotes("C:\\Users\\jaydu\\eclipse-workspace\\fundamentals\\src\\fundamentals\\voting.dat");
            electionSystem.displayResults();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }
}
