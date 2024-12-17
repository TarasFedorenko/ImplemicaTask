package com.implemica.graphTask.service;

import com.implemica.graphTask.entity.Highway;
import com.implemica.graphTask.entity.Town;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Graph {
    private int maxTowns; // The maximum number of towns in the graph
    private final int maxCost = 200000; // The maximum cost to travel between towns
    private Town[] townsList; // Array to store town objects
    private int[][] connectionTown; // Adjacency matrix to store connections and costs between towns
    private int countOfTown; // Counter for towns in the graph
    private int countOfTownInList; // Counter for active towns during path calculations
    private List<Highway> shortestPaths; // List of shortest paths for the current calculation
    private int currentTown; // The current town being processed
    private int startToRide; // Accumulated cost to the current town

    public void parseGraph() {
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {

            // Read the first number to determine how many iterations to perform
            int iterations = Integer.parseInt(reader.readLine());
            if (iterations >= 10) {
                throw new RuntimeException("The first number must be less than 10");
            }

            // Process each iteration
            for (int iteration = 0; iteration < iterations; iteration++) {
                if (!reader.ready()) {
                    throw new RuntimeException("Insufficient data for the specified number of iterations");
                }

                // Read the number of towns
                maxTowns = Integer.parseInt(reader.readLine());
                fillArray();

                // Read towns and their neighbors
                for (int i = 0; i < maxTowns; i++) {
                    String nameOfTown = reader.readLine();
                    addTown(nameOfTown, i); // Add the town to the list
                    int countOfNeighbours = Integer.parseInt(reader.readLine());
                    for (int j = 0; j < countOfNeighbours; j++) {
                        String pathToNeighbour = reader.readLine();
                        String[] townAndCost = pathToNeighbour.split(" ");
                        if (townAndCost.length != 2) {
                            throw new RuntimeException("Incorrect data format for town " + nameOfTown);
                        }
                        addCost(i, Integer.parseInt(townAndCost[0]) - 1, Integer.parseInt(townAndCost[1]));
                    }
                }

                // Read and process the paths to calculate the shortest path between towns
                int countOfPath = Integer.parseInt(reader.readLine());
                for (int j = 0; j < countOfPath; j++) {
                    String townsToFindWay = reader.readLine();
                    String[] wayBetweenTowns = townsToFindWay.split(" ");
                    if (wayBetweenTowns.length != 2) {
                        throw new RuntimeException("Incorrect data format for directions " + townsToFindWay);
                    }
                    Town startTown = Arrays.stream(townsList)
                            .filter(town -> town.getName().equals(wayBetweenTowns[0]))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Cannot find start town " + wayBetweenTowns[0]));

                    Town endTown = Arrays.stream(townsList)
                            .filter(town -> town.getName().equals(wayBetweenTowns[1]))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Cannot find end town " + wayBetweenTowns[1]));
                    shortestWay(startTown.getNumberOfTown(), endTown.getNumberOfTown(), writer);
                    clean(); // Reset for the next path calculation
                }

                writer.flush(); // Clear the buffer after each iteration
            }

        } catch (IOException e) {
            throw new RuntimeException("Data reading error", e);
        }
    }

    // Adds a town to the list
    private void addTown(String name, int numberOfTown) {
        townsList[countOfTown++] = new Town(name, numberOfTown);
    }

    // Adds a cost for traveling between two towns
    private void addCost(int start, int end, int cost) {
        connectionTown[start][end] = cost;
    }

    // Initializes arrays and data structures for the graph
    private void fillArray() {
        townsList = new Town[maxTowns];
        connectionTown = new int[maxTowns][maxTowns];
        countOfTown = 0;
        countOfTownInList = 0;
        for (int i = 0; i < maxTowns; i++) {
            for (int k = 0; k < maxTowns; k++) {
                connectionTown[i][k] = maxCost; // Set initial cost to max
                shortestPaths = new ArrayList<>();
            }
        }
    }

    // Finds the shortest path between two towns using Dijkstra's algorithm
    private void shortestWay(int startGraph, int endGraph, BufferedWriter writer) {
        townsList[startGraph].setActive(true);
        countOfTownInList = 1;

        // Initialize paths from the starting town
        for (int i = 0; i < countOfTown; i++) {
            int tempCost = connectionTown[startGraph][i];
            Highway cityPath = new Highway(tempCost);
            cityPath.getLeftBehindTowns().add(startGraph);
            shortestPaths.add(cityPath);
        }

        // Process towns to find the shortest path
        while (countOfTownInList < countOfTown) {
            int indexMin = getMin();
            int minDist = shortestPaths.get(indexMin).getCost();
            if (minDist == maxCost) {
                break;
            } else {
                currentTown = indexMin;
                startToRide = shortestPaths.get(indexMin).getCost();
            }
            townsList[currentTown].setActive(true);
            countOfTownInList++;
            updateShortestPaths();
        }
        displayPaths(startGraph, endGraph);
        writePaths(startGraph, endGraph, writer);
    }

    // Finds the index of the town with the minimum cost
    private int getMin() {
        int minDist = maxCost;
        int indexMin = 0;
        for (int i = 1; i < countOfTown; i++) {
            if (!townsList[i].isActive() && shortestPaths.get(i).getCost() < minDist) {
                minDist = shortestPaths.get(i).getCost();
                indexMin = i;
            }
        }
        return indexMin;
    }

    // Updates the shortest paths for towns not yet processed
    private void updateShortestPaths() {
        int townIndex = 1;
        while (townIndex < countOfTown) {
            if (townsList[townIndex].isActive()) {
                townIndex++;
                continue;
            }
            int currentToFringe = connectionTown[currentTown][townIndex];
            int startToFringe = startToRide + currentToFringe;
            int shortPathDistance = shortestPaths.get(townIndex).getCost();
            if (startToFringe < shortPathDistance) {
                List<Integer> newParents = new ArrayList<>(shortestPaths.get(currentTown).getLeftBehindTowns());
                newParents.add(currentTown);
                shortestPaths.get(townIndex).setLeftBehindTowns(newParents);
                shortestPaths.get(townIndex).setCost(startToFringe);
            }
            townIndex++;
        }
    }

    // Writes the shortest path to the output file
    private void writePaths(int startTown, int endTown, BufferedWriter writer) {
        String shortestWay = "";
        shortestWay = townsList[startTown].getName() + " -> " + townsList[endTown].getName() + " = ";
        if (shortestPaths.get(endTown).getCost() == maxCost) {
            shortestWay = shortestWay + "0";
        } else {
            String result = shortestPaths.get(endTown).getCost() + " (";
            List<Integer> parents = shortestPaths.get(endTown).getLeftBehindTowns();
            for (int j = 0; j < parents.size(); j++) {
                result += townsList[parents.get(j)].getName() + " -> ";
            }
            shortestWay = shortestWay + result + townsList[endTown].getName() + ")";
        }
        try {
            writer.write(shortestWay);
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Displays the shortest path in the console
    private void displayPaths(int startTown, int endTown) {
        System.out.print(townsList[startTown].getName() + " -> " + townsList[endTown].getName() + " = ");
        if (shortestPaths.get(endTown).getCost() == maxCost) {
            System.out.println("0");
        } else {
            String result = shortestPaths.get(endTown).getCost() + " (";
            List<Integer> parents = shortestPaths.get(endTown).getLeftBehindTowns();
            for (int j = 0; j < parents.size(); j++) {
                result += townsList[parents.get(j)].getName() + " -> ";
            }
            System.out.println(result + townsList[endTown].getName() + ")");
        }
    }

    // Resets variables and data structures for the next iteration
    private void clean() {
        countOfTownInList = 0;
        for (int i = 0; i < countOfTown; i++) {
            townsList[i].setActive(false);
        }
        countOfTownInList = 0;
        for (int i = 0; i < maxTowns; i++) {
            for (int k = 0; k < maxTowns; k++) {
                shortestPaths = new ArrayList<>();
            }
        }
    }
}
