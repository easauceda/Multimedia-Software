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
        Image img = new Image(args[1]);
        Scanner input = new Scanner(System.in);

        if(args.length != 2)
        {
            usage();
            System.exit(1);
        }

        switch(Integer.parseInt(args[0])){
            case 1:
                homeworkOne(input, img);
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
                    grey.display(grey.title  + "-grayscale.ppm");
                    grey.write2PPM(grey.title  + "-grayscale.ppm");
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
        int total;
        int avg;

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
            img = nLevelConversion(img, input);
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

    public static Image nLevelConversion(Image img, Scanner input){
        int choice = 0;
        boolean valid_choice = false;
        while(!valid_choice){
            System.out.println("--Conversion of a 24-Bit Color to a N-level");
            System.out.println("Input a value for N");
            choice = validateInput(input.nextLine());
            switch (choice) {
                case 2:
                    valid_choice = true;
                    img = errorDiffusion(img, 2);
                    img.display("Tester");
                    break;
                case 4:
                    valid_choice = true;
		            img = errorDiffusion(img, 4);
                    img.display("Tester");
                    break;
                case 8:
                    valid_choice = true;
		            img = errorDiffusion(img, 8);
                    img.display("Tester");
                    break;
                case 16:
                    valid_choice = true;
		            img = errorDiffusion(img, 16);
                    img.display("Tester");
                    break;
            }
        }
        return img;
    }

    public static Image errorDiffusion(Image img, int N){
        int new_val = 0;
        int[] RGBArray = new int[3];
        int[] gr = new int[3];
        int[] error = new int[3];
        int[] neighbor = new int[3];
	    int[] levels = getLevels(N);

        for (int y = 0; y < img.getH(); y++){
            for (int x = 0; x < img.getW(); x++) {
                img.getPixel(x, y, RGBArray);

                new_val = closestPixel(levels, RGBArray[0]);
                int er = (RGBArray[0] - new_val);

                Arrays.fill(gr, new_val);
                img.setPixel(x, y, gr);
                System.out.println(new_val);
                if (x + 1 < img.getW()){
                    int e = (int) (er * (7.0 / 16));
                    img.getPixel(x + 1, y, neighbor);
                    Arrays.fill(error, e + neighbor[0]);
                    img.setPixel(x + 1, y, error);
                }

                if (y + 1 < img.getH()){
                    int e = (int) (er * (5.0 / 16));
                    img.getPixel(x, y + 1, neighbor);
                    Arrays.fill(error, e + neighbor[0]);
                    img.setPixel(x, y + 1, error);
                }

                if (x - 1 >= 0 && y + 1 < img.getH()){
                    int e = (int) (er * (3.0 / 16));
                    img.getPixel(x - 1, y + 1, neighbor);
                    Arrays.fill(error, e + neighbor[0]);
                    img.setPixel(x - 1, y + 1, error);
                }

                if (x + 1 < img.getW() && y + 1 < img.getH()){
                    int e = (int) (er * (1.0 / 16));
                    img.getPixel(x + 1, y + 1, neighbor);
                    Arrays.fill(error, e + neighbor[0]);
                    img.setPixel(x + 1, y + 1, error);
                }
            }
        }
        return img;
    }

	public static int[] getLevels(int N){
		int[] levels = new int[N];
		for (int i = 0; i < N; i++){
			double val = 255 * i / (N - 1.0);
			levels[i] =  (int) val;
		}
		return levels;
	}
	
	public static int closestPixel(int[] levels, int px){
		int distance = 999;
		int closestPixel = 0;
		for (int level : levels){
            int distanceFromLevel = Math.abs(level - px);
            if (distanceFromLevel < distance){
                closestPixel = level;
                distance = distanceFromLevel;
            }
		}
		return closestPixel;
	}

}	
//TODO: Refactor to create global variables such as RGBArray and width/height