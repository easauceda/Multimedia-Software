import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class homeworkFour {
    public static void blockBasedMotionComp(Image target, Image ref, int n, int p) {
        n = validate(n, "n");
        p = validate(p, "p");
        Image error_img = new Image(target.getW(), target.getH());
        ArrayList<int[]> motion_vectors = new ArrayList<int[]>();

        for (int y = 0; y < target.getH(); y += n){
            for (int x = 0; x < target.getW(); x += n){
                int[] a = new int[3];
                int[] b = new int[3];
                double grey_a, grey_b;
                double sum_of_block = 0;
                double best_msd = Integer.MAX_VALUE;
                int rx = 0, ry = 0;

                for (int search_y = y - p; search_y <= y + p; search_y++){
                    for (int search_x = x - p; search_x <= x + p; search_x++){
                        int target_y = y;

                        if (search_x >= 0 && search_x + n <= target.getW()){
                            if (search_y >= 0 && search_y + n <= target.getH()){

                                for (int ref_y = search_y; ref_y < search_y + n; ref_y++){

                                    int target_x = x;

                                    for (int ref_x = search_x; ref_x < search_x + n; ref_x++){

                                        target.getPixel(target_x, target_y, a);
                                        ref.getPixel(ref_x, ref_y, b);

                                        grey_a = grey_val(a);
                                        grey_b = grey_val(b);

                                        sum_of_block += Math.pow((grey_a - grey_b), 2.0);

                                        target_x++;

//                                        Sleep for debugging purposes
//                                        try {
//                                            Thread.sleep(100);
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                        }

                                    }
                                    target_y ++;
                                }

                                double prefix = (1.0 / (n * n));
                                double msd = prefix * sum_of_block;

                                if (msd < best_msd){
                                    best_msd = msd;
                                    rx = search_x;
                                    ry = search_y;
                                }

                                sum_of_block = 0;

                            }
                        }

                    }
                }

                System.out.println("Best Match: " + rx + ", " + ry);
                System.out.println("MSD: " + best_msd);
                int motion_vector_x = (x - rx);
                int motion_vector_y = (y - ry);
                int[] motion_v = new int[]{motion_vector_x, motion_vector_y};
                motion_vectors.add(motion_v);
                
                save_motion_vectors(motion_vectors);

                //get error block
                int[] target_rgb = new int[3];
                int[] ref_rgb = new int[3];
                int[] error_rgb = new int[3];
                int error_ry = ry;

                for (int error_y = y; error_y < y + n; error_y++){

                    int error_rx = rx;

                    for (int error_x = x; error_x < x + n; error_x++){

                        target.getPixel(error_x, error_y, target_rgb);
                        ref.getPixel(error_rx, error_ry, ref_rgb);

                        //System.out.println("Target Pixel: " + error_x + ", " + error_y);
                        //System.out.println("Corresponding: " + error_rx + ", " + error_ry);

                        int error_r = Math.abs(target_rgb[0] - ref_rgb[0]);
                        //System.out.println(error_r);
                        int error_g = Math.abs(target_rgb[1] - ref_rgb[1]);
                        //System.out.println(error_g);
                        int error_b = Math.abs(target_rgb[2] - ref_rgb[2]);
                        //System.out.println(error_b);
                        //System.out.println();

                        error_rgb[0] = error_img.limitCheck(error_r, 0, 255);
                        error_rgb[1] = error_img.limitCheck(error_g, 0, 255);
                        error_rgb[2] = error_img.limitCheck(error_b, 0, 255);

                        //System.out.println(error_rgb[0]+ " ," + error_rgb[1] + ", " + error_rgb[2]);

                        error_img.setPixel(error_x, error_y, error_rgb);

                        error_rx++;
                    }

                    error_ry++;

                }

            }
        }
        error_img.display("Error Image");
    }

    private static void save_motion_vectors(ArrayList<int[]> motion_vectors) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("motion_vectors.txt"));
            for (int[] vector : motion_vectors){
                out.write("[" + vector[0] + ", " + vector[1] + "]");
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Something went wrong.");
        }
    }

    public static int validate(int target, String param){
        if (param.equals("n")){
            if (target != 8 && target != 16 && target != 32){
                System.out.println("Value is incorrect. Defaulting to 8.");
                target = 8;
            }
        }
        if (param.equals("p")){
            if (target != 4 && target != 8 && target != 12){
                System.out.println("Value is incorrect. Defaulting to 4.");
                target = 4;
            }
        }
        return target;
    }

    public static double grey_val(int[] rgb){
        return Math.round(0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);
    }

    public static void removeMovingObjects(int n) {
        //validate chosen frame
        if (n < 19){
            System.out.println("Too low. Defaulting to 19.");
            n = 19;
        } else if (n > 179){
            System.out.println("Too high. Defaulting to 179.");
            n = 179;
        }
        //format chosen frame & reference frame
        String chosen_frame = String.format("IDB/Walk_%03d.ppm",n);
        String ref_frame = String.format("IDB/Walk_%03d.ppm",n - 2);
        System.out.println(chosen_frame);
        System.out.println(ref_frame);
    }
}
