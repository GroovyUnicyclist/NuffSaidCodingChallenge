package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NuffSaidCodingChallenge {
    public static void main(String[] args) {
        String csvFile = "Data/school_data.csv";
        String line;
        String header;

        //ArrayList of String arrays for each row in csv
        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            header = br.readLine();
            data.add(separateData(header));
            while ((line = br.readLine()) != null) {
                data.add(separateData(line));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

//        printEntry(data, 0);
//        printEntry(data, 1);


        // Part 1

        System.out.println("Total Schools: " + (data.size()-1));

        System.out.println("\nSchools by State:");
        Map<String, Integer> stateCountMap = countSchoolsByCol(data, 5);
        Iterator<Map.Entry<String, Integer>> it = stateCountMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Integer> mapEnt = it.next();
            System.out.println(mapEnt.getKey() + ": " + mapEnt.getValue());
            it.remove();
        }

        System.out.println("\nSchools by Metro-centric locale:");
        Map<String, Integer> metroCentricCountMap = countSchoolsByCol(data, 8);
        it = metroCentricCountMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Integer> mapEnt = it.next();
            System.out.println(mapEnt.getKey() + ": " + mapEnt.getValue());
            it.remove();
        }

        Map<String, Integer> cityCountMap = countSchoolsByCol(data, 4);
        int maxVal = 0;
        String maxKey = "";
        int oneSchoolCount = 0;
        Set<String> discoveredCities = new HashSet<>();
        it = cityCountMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Integer> mapEnt = it.next();
            String mpKey = mapEnt.getKey();
            int mpVal = mapEnt.getValue();

            if (mpVal>maxVal) {
                maxVal = mpVal;
                maxKey = mpKey;
            }

            if (!discoveredCities.contains(mpKey)) {
                oneSchoolCount++;
                discoveredCities.add(mpKey);
            }

            it.remove();
        }

        System.out.println("\nCity with most schools: " + maxKey + " (" + maxVal + " schools)");
        System.out.println("Unique cities with at least one school:" + oneSchoolCount);


        // Part 2
        String[] keywords;

        //Column data
        String[] schoolNames = new String[data.size()-1];
        String[] cityNames = new String[data.size()-1];
        String[] stateNames = new String[data.size()-1];

        //Multiple Column Data
        String[] dataKeywords = new String[data.size()-1];

        // Accuracy scores
        int[] scores = new int[data.size()-1];

        // Initialize arrays
        for (int i = 1; i<data.size(); i++) {
            schoolNames[i-1] = data.get(i)[3];
            cityNames[i-1] = data.get(i)[4];
            stateNames[i-1] = data.get(i)[5];
            dataKeywords[i-1] = schoolNames[i-1] + " " + cityNames[i-1] + " " + stateNames[i-1];
        }


        Scanner scan = new Scanner(System.in);
        System.out.println("\nEnter a Search Query (or press enter to end program):");
        String input = scan.nextLine().toLowerCase();

        while (!input.equals("")) {
            // Unnecessary method
//            keywords = separateKeywords(input);

            keywords = input.split(" ");

            int topScore = 0;
            int numTopScores = 0;
            List<Integer> topScores = new ArrayList<>();

            // Query
            long startTime = System.currentTimeMillis();
            for (int i = 0; i<schoolNames.length; i++) {
                scores[i] = calculateScore(dataKeywords[i], keywords);
                if (scores[i]>0) {
                    if (scores[i]>scores[topScore]) {
                        topScore = i;
                        topScores.add(0, i);
                        numTopScores = 1;
                    } else if(scores[i]==scores[topScore]) {
                        topScores.add(numTopScores, i);
                        numTopScores++;
                    }
                }
            }
            long endTime = System.currentTimeMillis();

            System.out.println("Results for \"" + input + "\" (search took: " + ((double)endTime - (double)startTime)/1000 + "s)");

            for (int i = 1; i <= 3 && i <= topScores.size(); i++) {
                System.out.println(i + ". " + schoolNames[topScores.get(i-1)] + "\n"
                        + cityNames[topScores.get(i-1)] + ", " + stateNames[topScores.get(i-1)]);
            }

            System.out.println("\nEnter a Search Query (or press enter to end program): ");
            input = scan.nextLine().toLowerCase();
        }

    }

    private static int calculateScore(String data, String[]keywords) {
        int score = 0;
        int index;
        for (int i = 0; i<keywords.length; i++) {
            // 1 point if keyword matches, 1 more if word is not "school"
            if (data.toLowerCase().indexOf(keywords[i])>=0) {
                score++;
                if (!keywords[i].equals("school")) {
                    score++;
                }
            }
        }
        return score;
    }

    private static String[] separateData(String input) {
        int pos = 0;
        List<String> dataArr = new ArrayList<>();
        String[] output;
        int start;

        //If quotations are found, continues until next quotation, else splits data by commas
        while (input.length()>pos) {
            start = pos;
            if (input.charAt(start) == '"') {
                pos++;
                while (input.length()>pos && input.charAt(pos) != '"') {
                    pos++;
                }
                pos++;
            } else {
                while (input.length()>pos && input.charAt(pos) != ',') {
                    pos++;
                }
            }
            dataArr.add(input.substring(start,pos).trim());
            pos++;
        }
        output = new String[dataArr.size()];
        dataArr.toArray(output);
        return output;
    }

    private static Map<String, Integer> countSchoolsByCol(List<String[]> data, int col) {
        String[] entry;
        String colData;
        int count;
        Map<String, Integer> dataMap = new HashMap<>();

        // Sets dataMap entries with keys as each unique column data and values as the number of rows with identical column data
        for (int i = 1; i<data.size(); i++) {
            entry = data.get(i);
            colData = entry[col];
            if (dataMap.containsKey(colData)) {
                count = dataMap.get(colData)+1;
                dataMap.remove(colData);
                dataMap.put(colData,count);
            } else {
                dataMap.put(colData,1);
            }
        }
        return dataMap;
    }

    private static void printEntry(List<String[]> data, int index) {
        String[] entry = data.get(index);
        for (int i = 0; i < entry.length; i++) {
            System.out.print(entry[i]+ "   ");
        }
        System.out.println();
    }

    //Overcomplicated method
    private static String[] separateKeywords(String input) {
        int pos = 0;
        List<String> keywordArr = new ArrayList<>();
        String[] output;
        String word = "";

        //Builds words letter by letter, checking that only letters and numbers are used
        while (pos<input.length()) {
            char current = input.charAt(pos);
            if (current == ' ' && word.length()>0) {
                keywordArr.add(word);
                word = "";
            } else if ((current >= 48 && current <= 57) || (current >= 65 && current <= 90) || (current >= 97 && current <= 122)) {
                word += current;
            }
            pos++;
        }
        if (word.length()>0) {
            keywordArr.add(word);
        }
        output = new String[keywordArr.size()];
        keywordArr.toArray(output);
        return output;
    }
}
