import java.awt.image.BufferedImage;
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
    public static void main(String[] args)
    {
        if(args.length != 1)
        {
            usage();
            System.exit(1);
        }

        Image img = new Image(args[0]);
        int choice = 0;
        Scanner input = new Scanner(System.in);

        while(choice != 4) {
            switch (choice) {
                case 1:
                    Image grey = convert24to8(img);
                    grey.write2PPM("Grayscale-" + args[0]);
                    grey.display("Grayscale-" + args[0]);
                    break;
                case 2:
                    Image NLevel = convert24toN(img, input);
                    NLevel.write2PPM("NLevel-" + args[0]);
                    NLevel.display("NLevel-" + args[0]);
                    break;
                case 3:
                    System.out.println("You chose 3");
                    break;
            }
            choice = displayMenu(input);
        }
        System.out.println("--Good Bye--");
        System.exit(0);
    }

    public static void usage()
    {
        System.out.println("\nUsage: java CS451_Sauceda [inputfile]\n");
    }

    public static int displayMenu(Scanner input){
        int choice;
        System.out.println("--Welcome to Multimedia Software System--");
        System.out.println("Main Menu--------------------------------");
        System.out.println("1. Conversion to Gray-scale Image (24bits->8bits)");
        System.out.println("2. Conversion to N-level image");
        System.out.println("3. Conversion to 8bit Indexed Color using Uniform Color Quantization (24bits->8bits)");
        System.out.println("4. Quit");
        System.out.println("Please enter the task number [1-4]");
        choice = validateInput(input.nextLine());
        return choice;
    }

    public static Image convert24to8(Image img){
        int[] RGBArray = new int[3];
        int[] gr = new int[3];
        Image gray = new Image(img.getW(), img.getH());
        for (int y = 0; y < img.getH(); y++){
            for (int x = 0; x < img.getW(); x++) {
                img.getPixel(x, y, RGBArray);
                int gr_val  = (int) Math.round(0.299 * RGBArray[0] + 0.587 * RGBArray[1] + 0.114 * RGBArray[2]);
                if (gr_val > 255) gr_val = 255;
                if (gr_val < 0) gr_val = 0;
                Arrays.fill(gr, gr_val);
                gray.setPixel(x, y, gr);
            }
        }
        return gray;
    }

    public static Image convert24toN(Image img, Scanner input){
        int choice = 0;
        int total;
        int avg;
        while(choice != 1 && choice != 2){
            System.out.println("--Conversion of a 24-Bit Color to a N-level");
            System.out.println("Input 1 for Bi-level using threshold or 2 for N-Level error diffusion");
            choice = validateInput(input.nextLine());
            System.out.println("Choice");
        }
        if (choice == 1){
            img = convert24to8(img);
            img = biLevelConversion(img);
        } else if (choice == 2) {
            System.out.println("N-level conversion");
        }
        return img;
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

    public static Image biLevelConversion(Image img){
        int[] RGBArray = new int[3];
        int[] gr = new int[3];
        int new_val = 0;
        int ga = img.getAvgPixel();
        for (int y = 0; y < img.getH(); y++){
            for (int x = 0; x < img.getW(); x++) {
                img.getPixel(x, y, RGBArray);
                System.out.println(RGBArray[0]);
                if (RGBArray[0] > ga){
                    new_val = 255;
                } else if (RGBArray[0] <= ga){
                    new_val = 0;
                }
                Arrays.fill(gr, new_val);
                img.setPixel(x, y, gr);
            }
        }
        return img;
    }
}