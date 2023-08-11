package Sockets;

import java.io.*;
import java.net.*;
import java.util.*;

public class socket {

    private static final int PORT = 12345;

    public static void main(String[] args) {
        int successes = 0;
        int failures = 0;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    processClientData(in, successes, failures);
                }
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + PORT);
            System.out.println(e.getMessage());
        }
    }

    public static void processClientData(BufferedReader in, int successes, int failures) throws IOException {
        String clientInput;
        while ((clientInput = in.readLine()) != null) {
            String[] inputData = clientInput.split(",");
            int action = Integer.parseInt(inputData[2]);
            String data = inputData[0];
            int modelChoice = Integer.parseInt(inputData[1]);

            if (modelChoice == 1) {
                data = CRCReceiver.processReceivedData(data);
            } else {
                System.out.println("Code for Hamming model");
            }

            if (action == 1) {
                processMessage(data);
            } else {
                countResults(data, successes, failures);
            }
        }
    }

    public static void processMessage(String data) {
        if (!"Error".equals(data)) {
            System.out.println(binaryToText(data));
        } else {
            System.out.println("Error: Message could not be processed.");
        }
    }

    public static void countResults(String data, int successes, int failures) {
        if (!"Error".equals(data)) {
            successes++;
            System.out.println("Successes: " + successes);
        } else {
            failures++;
            System.out.println("Failures: " + failures);
        }
    }

    public static String binaryToText(String binaryString) {
        StringBuilder textBuilder = new StringBuilder();
        for (int i = 0; i < binaryString.length(); i += 8) {
            String section = binaryString.substring(i, Math.min(i + 8, binaryString.length()));
            int decimalValue = Integer.parseInt(section, 2);
            textBuilder.append((char) decimalValue);
        }
        return textBuilder.toString();
    }

}

class CRCReceiver {

    private static final List<Integer> CRC_32_PATTERN = Arrays.asList(
            1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1);

    public static boolean isDataCorrupt(List<Integer> dataBits, List<Integer> crcPattern) {
        List<Integer> currentBits = new ArrayList<>(dataBits.subList(0, crcPattern.size()));

        for (int i = crcPattern.size(); i < dataBits.size(); i++) {
            if (currentBits.get(0) == 1) {
                for (int j = 0; j < crcPattern.size(); j++) {
                    currentBits.set(j, currentBits.get(j) ^ crcPattern.get(j));
                }
            }
            currentBits.remove(0);
            currentBits.add(dataBits.get(i));
        }

        if (currentBits.get(0) == 1) {
            for (int j = 0; j < crcPattern.size(); j++) {
                currentBits.set(j, currentBits.get(j) ^ crcPattern.get(j));
            }
        }

        return currentBits.subList(1, currentBits.size()).contains(1);
    }

    public static String processReceivedData(String inputData) {
        List<Integer> dataBits = new ArrayList<>();
        for (char bit : inputData.toCharArray()) {
            dataBits.add(Character.getNumericValue(bit));
        }

        boolean corrupt = isDataCorrupt(dataBits, CRC_32_PATTERN);
        List<Integer> dataWithoutCRC = dataBits.subList(0, dataBits.size() - CRC_32_PATTERN.size() + 1);

        if (corrupt) {
            return "Error";
        } else {
            StringBuilder result = new StringBuilder();
            for (int bit : dataWithoutCRC) {
                result.append(bit);
            }
            return result.toString();
        }
    }
}
