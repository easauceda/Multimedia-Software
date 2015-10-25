import java.util.Arrays;
import java.util.Scanner;

/*******************************************************
 CS451 Multimedia Software Systems
 @ Template_Author: Elaine Kang
 @ Author: Erick Sauceda
 *******************************************************/

// Template Code

public class CS451_Sauceda
{
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        switch(Integer.parseInt(args[0])){
            case 1:
                if(args.length != 2)
                {
                    usage();
                    System.exit(1);
                }

                Image img = new Image(args[1]);
                homeworkOne(input, img);
                break;
            case 2:
                homeworkTwo(input);
                break;
            default:
                usage();
                break;
        }
        System.out.println("--Good Bye--");
        System.exit(0);
    }

    public static void homeworkOne(Scanner input, Image img){
        int choice = 0;
        String[] options = new String[3];
        options[0] = "Conversion to Gray-scale Image (24bits -> 8bits)";
        options[1] = "Conversion to N-level Image";
        options[2] = "Conversion to 8bit Indexed Color Image using Uniform Color";

        while(choice != 4) {
            switch (choice) {
                case 1:
                    Image grey = img.convertToGrayscale();
                    grey.display(grey.title + "-grayscale.ppm");
                    grey.write2PPM(grey.title + "-grayscale.ppm");
                    break;
                case 2:
                    convert24toN(img, input);
                    break;
                case 3:
                    img.colorQuantization();
                    break;
            }
            choice = displayMenu(input, options);
        }
    }

    public static void homeworkTwo(Scanner input){
        int choice = 0;
        String[] options = new String[2];
        options[0] = "Aliasing";
        options[1] = "Dictionary Coding";

        while(choice != 3) {
            switch (choice) {
                case 1:
                    Image circles = new Image(512, 512);

                    System.out.println("Enter M (Thickness)");
                    int M = Integer.parseInt(input.nextLine());

                    System.out.println("Enter N (Difference between successive Radii");
                    int N = Integer.parseInt(input.nextLine());

                    System.out.println("Enter K");
                    int K = Integer.parseInt(input.nextLine());

                    circles.drawCircles(M, N, K);
                    break;
                case 2:
                    DictionaryCoder input_data = new DictionaryCoder();

                    try {

                        input_data.encode();

                    } catch (Exception e){

                        System.out.println("Something went wrong");

                    }
                    break;
            }
            choice = displayMenu(input, options);
        }
    }

    public static void usage()
    {
        System.out.println("\nUsage: java CS451_Sauceda [homework number] [inputfile]\n");
    }

    public static int displayMenu(Scanner input, String[] options){
        int choice;
        int i = 0;

        System.out.println("--Welcome to Multimedia Software System--");
        System.out.println("Main Menu--------------------------------");

        for (String option : options){
            i++;
            System.out.println(i + ". " + option);
        }
        i++;

        System.out.println(i + ". Quit");
        System.out.println("Please enter the task number [1-" + i+ "]");

        choice = validateInput(input.nextLine());

        return choice;
    }

    public static void convert24toN(Image img, Scanner input){
        int choice = 0;

        img = img.convertToGrayscale();

        while(choice != 1 && choice != 2){
            System.out.println("--Conversion of a 24-Bit Color to a N-level");
            System.out.println("Input 1 for Bi-level using threshold or 2 for N-Level error diffusion");
            choice = validateInput(input.nextLine());
        }
        if (choice == 1){
            img.biLevelConversion();
        } else if (choice == 2) {
            System.out.println("N-level conversion");
            nLevelConversion(img, input);
        }
    }

    public static Integer validateInput(String choice){
        int validated_input = -1;
        try {
            validated_input = Integer.parseInt(choice);
        } catch(Exception ex){
            System.out.println("Improper value. Try again");
        }
        return validated_input;
    }

    public static void nLevelConversion(Image img, Scanner input) {
        int choice;
        boolean valid_choice = false;
        while (!valid_choice) {
            System.out.println("--Conversion of a 24-Bit Color to a N-level");
            System.out.println("Input a value for N");
            choice = validateInput(input.nextLine());
            switch (choice) {
                case 2:
                    valid_choice = true;
                    img = img.errorDiffusion(img, 2);
                    img.display("Tester");
                    img.write2PPM("Tester.ppm");
                    break;
                case 4:
                    valid_choice = true;
                    img = img.errorDiffusion(img, 4);
                    img.display("Tester");
                    break;
                case 8:
                    valid_choice = true;
                    img = img.errorDiffusion(img, 8);
                    img.display("Tester");
                    break;
                case 16:
                    valid_choice = true;
                    img = img.errorDiffusion(img, 16);
                    img.display("Tester");
                    break;
            }
        }
    }

}	
//TODO: Refactor to create global variables such as RGBArray and width/height
//TODO: Move functions nLevelConversion & nLevelConversion from main class to support class