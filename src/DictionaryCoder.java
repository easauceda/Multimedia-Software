
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by esauceda on 10/23/15.
 */

public class DictionaryCoder {
    private Map<Integer, String> dictionary = new HashMap<Integer, String>();
    private ArrayList<String> message = new ArrayList<String>();
    String fileName;
    private ArrayList<Integer> encoded_message = new ArrayList<Integer>();
    String decoded_message = "";
    int dictionaryLim;

    public DictionaryCoder() {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter the filename");
        fileName = input.next();
        System.out.println("Enter the dictionary size");
        dictionaryLim = Integer.parseInt(input.next());

        try {

            FileInputStream fs = new FileInputStream(fileName);
            int next = fs.read();
            while (next != -1) {
                String next_char = Character.toString((char) next);
                message.add(next_char);
                next = fs.read();

            }

        } catch (Exception e) {
            System.out.println("Something went wrong");
        }

    }

    public void encode() {
        buildDictionary(message);
        String sequence = "";
        int oldKey;
        int key = 0;

        for (int i = 0; i < message.size(); i++) {

            sequence += message.get(i);
            oldKey = key;
            key = inDictionary(sequence);

            //TODO: Add size constraint that can be set by user

            if (key == -1) {
                encoded_message.add(encoded_message.size(), oldKey);
                if (dictionary.size() < dictionaryLim) {
                    dictionary.put(dictionary.size(), sequence);
                }
                sequence = "";
                i--;
            }
            if (i == message.size() - 1) {
                encoded_message.add(encoded_message.size(), key);
            }
        }

        System.out.println("Compression Ratio: " + ((double) (message.size() * 8) / (8 * encoded_message.size())));

        try {
            writeEncoded();
        } catch (Exception e) {
            System.out.println("Something went wrong.");

        }
    }

    public void decode() {
        //System.out.println("Decoding: ");
        buildDictionary(message);
        for (int i = 0; i < encoded_message.size(); i++) {

            int key = encoded_message.get(i);

            if (dictionary.containsKey(key)) {
                decoded_message += dictionary.get(key);
            }

            if (i + 1 < encoded_message.size()) {

                int next_key = encoded_message.get(i + 1);

                if (dictionary.containsKey(next_key)) {

                    dictionary.put(dictionary.size(), dictionary.get(key) + dictionary.get(next_key).charAt(0));

                } else {

                    dictionary.put(dictionary.size(), dictionary.get(key) + dictionary.get(key).charAt(0));

                }
            }

        }

    }

    public Integer inDictionary(String sequence) {
        int noMatch = -1;

        for (Map.Entry<Integer, String> entry : dictionary.entrySet()) {

            if (sequence.equals(entry.getValue())) {

                return entry.getKey();
            }
        }
        return noMatch;
    }

    public void buildDictionary(ArrayList<String> message) {
        int index = 0;
        dictionary.clear();

        for (String entry : message) {
            if (!dictionary.containsValue(entry)) {
                dictionary.put(index, entry);
                index++;
            }
        }
    }

    private void writeEncoded() throws Exception {
        PrintWriter outstream = new PrintWriter("encoded.txt");
        double compressionRatio = ((double) (message.size() * 8) / (8 * encoded_message.size()));

        outstream.println(fileName + " Results: \n");

        outstream.println("\nOriginal Text: ");

        for (String message_char : message) {

            outstream.write(message_char);
        }

        outstream.println("\n\nIndex       Entry");
        outstream.println("-----------------");


        for (int i = 0; i < dictionary.size(); i++) {
            outstream.println(i + "             " + dictionary.get(i));
        }

        outstream.println("\nEncoded Message : ");
        for (int encoded_int : encoded_message) {
            outstream.write(encoded_int + " ");
        }


        decode();
        outstream.println("\n\nDecoded Message: ");
        outstream.println(decoded_message);

        outstream.println("Compression Ratio: " + compressionRatio);


        //Write outputs to the console

        System.out.println("Compression Ratio: " + compressionRatio);
        System.out.println("Data Size Before Compression: " + (message.size() * 8));
        System.out.println("Data Size After Compression: " + (8 * encoded_message.size()));

        outstream.flush();
        outstream.close();

    }
}
