
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
    private ArrayList<String> encoded_message = new ArrayList<String>();
    private int fileSize = 0;

    public DictionaryCoder(){
        Scanner input = new Scanner(System.in);

        System.out.println("Enter the filename");
        fileName = input.next();

        try {

            FileInputStream fs = new FileInputStream(fileName);

            //Read the file into memory
            int next = fs.read();
            while (next != -1){

                message.add(Character.toString((char) next));
                next = fs.read();
                fileSize += 8;

            }

        } catch (Exception e){
            System.out.println("Something went wrong");
        }

    }

    public void encode(){
        buildDictionary(message);
        String sequence = "";
        int oldKey;
        int key = 0;

        for (int i = 0; i < message.size(); i++){

            sequence += message.get(i);
            oldKey = key;
            key = inDictionary(sequence);

            if (key == -1){
                encoded_message.add(encoded_message.size(), Integer.toString(oldKey));
                if (dictionary.size() < 256){
                    dictionary.put(dictionary.size(), sequence);
                }
                sequence = "";
                i--;
            }
            if (i == message.size() - 1){
                encoded_message.add(encoded_message.size(), Integer.toString(key));
            }
        }

        System.out.println("Compression Ratio: " +  ((double) (message.size() * 8) / (8 * encoded_message.size())));

        try {
            writeEncoded();
        } catch (Exception e) {
            System.out.println("Something went wrong.");

        }
    }

    public void decode(){
        //this janky ass code
        buildDictionary(message);
        for (int i = 0; i < encoded_message.size(); i++){

            int K = Integer.parseInt(encoded_message.get(i));
            System.out.print(dictionary.get(K));

            if (i + 1 < encoded_message.size()){

                if (inDictionary(encoded_message.get(i + 1)) != -1){

                    int future_K = Integer.parseInt(encoded_message.get(i + 1));
                    dictionary.put(dictionary.size(), dictionary.get(K) + dictionary.get(future_K).substring(0,1));

                } else {

                    dictionary.put(dictionary.size(), dictionary.get(K) + dictionary.get(K).substring(0,1));

                }

            }
        }

    }

    public Integer inDictionary(String sequence){
        int noMatch = -1;
        for (Map.Entry<Integer, String> entry : dictionary.entrySet()){
            if (sequence.equals(entry.getValue())){
                return entry.getKey();
            }
        }
        return noMatch;
    }

    public void buildDictionary(ArrayList<String> message){
        int index = 0;
        dictionary.clear();

        for (String entry : message){
            if (!dictionary.containsValue(entry)){
                dictionary.put(index, entry);
                index++;
            }
        }
    }

    private void writeEncoded() throws Exception{
        PrintWriter outstream = new PrintWriter("encoded.txt");

        outstream.println(fileName + " Results: \n");

        outstream.println("\nOriginal Text: ");

        for (String message_char : message){
            outstream.write(message_char);
        }

        outstream.println("\n\nIndex       Entry");
        outstream.println("-----------------");

        for (Map.Entry<Integer, String> entry : dictionary.entrySet()){
            outstream.println(entry.getKey() + "             " + entry.getValue());
        }

        outstream.println("\nEncoded Message : ");
        for (String encoded_char : encoded_message){
            outstream.write(encoded_char + " ");
        }


        outstream.flush();
        outstream.close();

    }
}
