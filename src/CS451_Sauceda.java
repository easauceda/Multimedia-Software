import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Scanner;

/*******************************************************
 CS451 Multimedia Software Systems
 @ Author: Elaine Kang
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

        while(choice != 4) {
            switch (choice) {
                case 1:
                    convert24to8(img).write2PPM("Grayscale-" + args[0]);
                    break;
                case 2:
                    System.out.println("You chose 2");
                    break;
                case 3:
                    System.out.println("You chose 3");
                    break;
            }
            choice = displayMenu();
        }
        img.display(args[0]+"-out");
        img.write2PPM("out.ppm");
        System.out.println("--Good Bye--");
    }

    public static void usage()
    {
        System.out.println("\nUsage: java CS451_Sauceda [inputfile]\n");
    }

    public static int displayMenu(){
        int choice = 0;
        Scanner input = new Scanner(System.in);
        System.out.println("--Welcome to Multimedia Software System--");
        System.out.println("Main Menu--------------------------------");
        System.out.println("1. Conversion to Gray-scale Image (24bits->8bits)");
        System.out.println("2. Conversion to N-level image");
        System.out.println("3. Conversion to 8bit Indexed Color using Uniform Color Quantization (24bits->8bits)");
        System.out.println("4. Quit");
        System.out.println("Please enter the task number [1-4]");
        try {
            choice = Integer.parseInt(input.nextLine());
        } catch(Exception ex){
            System.out.println("Improper value. Try again");
        }
        return choice;
    }

    public static Image convert24to8(Image img){
        int[] RGBArray = new int[3];
        int[] gr = new int[3];
        Image gray = new Image(img.getW(), img.getH());
        for (int y = 0; y < img.getH(); y++){
            for (int x = 0; x < img.getW(); x++){
                img.getPixel(x,y, RGBArray);
                int gr_val  = (byte) Math.round(0.299 * RGBArray[0] + 0.587 * RGBArray[1] + 0.114 * RGBArray[2]);
                Arrays.fill(gr, gr_val);
                gray.setPixel(x, y, gr);
            }
        }
        return gray;
    }
}