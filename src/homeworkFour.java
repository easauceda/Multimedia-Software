import java.io.*;
import java.util.*;


public class homeworkFour {
    public static int[][][] blockBasedMotionComp(Image target, Image ref, int n, int p, boolean user) {
        n = validate(n, "n");
        p = validate(p, "p");
        Image error_img = new Image(target.getW(), target.getH());
        int[][][] motion_vectors = new int[target.getW() / 8][target.getH() / 8][2];

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

                //System.out.println("Best Match: " + rx + ", " + ry);
                //System.out.println("MSD: " + best_msd);
                int motion_vector_x = (x - rx);
                int motion_vector_y = (y - ry);
                int[] motion_v = new int[]{motion_vector_x, motion_vector_y};
                motion_vectors[x / 8][y / 8] = motion_v;
                
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
        if(user){
            error_img.display("Error Image");
        }
        return motion_vectors;
    }
    public static int[][][] blockBasedMotionComp(Image target, Image ref, int n, int p){
        return blockBasedMotionComp(target, ref, n, p, false);
    }


    private static void save_motion_vectors(int[][][] motion_vectors) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("motion_vectors.txt"));
            for (int[][] x : motion_vectors){
                for (int [] vector : x){
                    out.write("[" + vector[0] + ", " + vector[1] + "]");
                    out.newLine();
                }
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

        Image chosen = new Image(chosen_frame);
        Image ref = new Image(ref_frame);
        Image five = new Image("IDB/Walk_005.ppm");

        removeMovingObjectsMethodOne(chosen, ref, blockBasedMotionComp(chosen, ref, 8, 4));
        removeMovingObjectsMethodTwo(chosen, five, blockBasedMotionComp(chosen, ref, 8, 4));


    }

    public static void removeMovingObjectsMethodTwo(Image target, Image ref, int[][][] motion_vectors) {
        Image finImg = new Image(target.getW(), target.getH());
        int a;
        int b = 0;
        for (int y = 0; y < target.getH(); y += 8) {
            a = 0;
            for (int x = 0; x < target.getW(); x += 8) {
                int[] cur_mv = motion_vectors[a][b];
                if (cur_mv[0] != 0 || cur_mv[1] != 0) {
                    for (int j = y; j < y + 8; j++) {
                        for (int i = x; i < x + 8; i++) {
                            int[] refRGB = new int[3];
                            ref.getPixel(i, j, refRGB);
                            finImg.setPixel(i, j, refRGB);
                        }
                    }
                } else {
                    for (int j = y; j < y + 8; j++) {
                        for (int i = x; i < x + 8; i++) {
                            int[] targetRGB = new int[3];
                            target.getPixel(i, j, targetRGB);
                            finImg.setPixel(i, j, targetRGB);
                        }
                    }
                }
                a++;
            }
            b++;
        }
        target.display("Original");
        finImg.display("Motion Removed Method Two");
    }

    public static void removeMovingObjectsMethodOne(Image target, Image ref, int[][][] motion_vectors) {
        Image finImg = new Image(target.getW(), target.getH());
        int a;
        int b = 0;
        int good_x = 0;
        int good_y = 0;

        for (int y = 0; y < target.getH(); y += 8) {
            a = 0;
            for (int x = 0; x < target.getW(); x += 8) {

                int[] cur_mv = motion_vectors[a][b];
                if (cur_mv[0] != 0 || cur_mv[1] != 0) {
                    int ry = good_y;
                    for (int j = y; j < y + 8; j++) {
                        int rx = good_x;
                        for (int i = x; i < x + 8; i++) {
                            int[] refRGB = new int[3];
                            ref.getPixel(rx, ry, refRGB);
                            finImg.setPixel(i, j, refRGB);
                            rx++;
                        }
                        ry++;
                    }
                } else {
                    for (int j = y; j < y + 8; j++) {
                        for (int i = x; i < x + 8; i++) {
                            int[] targetRGB = new int[3];
                            target.getPixel(i, j, targetRGB);
                            finImg.setPixel(i, j, targetRGB);
                        }
                    }
                    good_x = x;
                    good_y = y;
                }
                a++;
            }
            b++;
        }
        target.display("Original");
        finImg.display("Motion Removed Method One");
    }


    public static void findThreeSimilar(Image target) {
        double[] percentages = new double[]{0,0,0};
        String[] names = new String[3];

        int total_pts = (target.getW() * target.getH()) / 64;
        for (int i = 1; i <= 200; i++){
            double sim_pts = total_pts;
            String name = String.format("IDB/Walk_%03d.ppm", i);
            Image ref = new Image(name);
            int[][][] motion_vectors = blockBasedMotionComp(target, ref, 8, 4);
            for (int[][] row : motion_vectors){
                for (int[] mv : row){
                    if (mv[0] != 0 || mv[1] != 0){
                        sim_pts--;
                    }
                }
            }

            double similarity = sim_pts / total_pts;
            if (percentages[0] < similarity){

                percentages[2] = percentages[1];
                names[2] = names[1];

                percentages[1] = percentages[0];
                names[1] = names[0];

                percentages[0] = similarity;
                names[0] = name;
            } else if (percentages[1] < similarity){

                percentages[2] = percentages[1];
                names[2] = names[1];

                percentages[1] = similarity;
                names[1] = name;

            } else if (percentages[2] < similarity){

                percentages[2] = similarity;
                names[2] = name;

            }
        }
        for (int num = 0; num < 3; num++){
            String similar = String.format("File: %s, Similarity: %f", names[num], percentages[num]);
            System.out.println(similar);
        }
    }
}
