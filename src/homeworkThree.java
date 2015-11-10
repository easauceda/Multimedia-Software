import java.util.Scanner;

/**
 * Created by esauceda on 11/4/15.
 */
public class homeworkThree {
    public static void start(String filename, Scanner input){
        //F1. Read and resize the input image
        //Read the image
        Image origImg = new Image(filename);
        //Instantiate img we will work with
        Image workImg;
        //Check height
        int origImgH = origImg.getH();
        //Check width
        int origImgW = origImg.getW();
        //if height % 8 != 0 || width % 8 != 0
        if (origImgH % 8 != 0 || origImgW % 8 != 0) {
            int resizeH = (int) Math.ceil(origImgH / 8.0) * 8;
            int resizeW = (int) Math.ceil(origImgW / 8.0) * 8;

            workImg = new Image(resizeW, resizeH);
            workImg.copy(origImg, origImgW, origImgH);
            workImg.padImage(origImgW, origImgH);

        } else {
            workImg = origImg;
        }
        workImg.display("Resized Image");
        //I4. I4. Remove Padding and Display the image
        workImg.dePad(origImgW, origImgH);
        workImg.display("Decompressed Image");

        //F2. Color space transformation and Subsampling

            //Transform pixels
        double[][][] yCbCr = workImg.transformYCbCr();
        //Subsample using 4:2:0
            //4 = J, this is how wide the blocks are
        double[][][] subYCbCr = workImg.subsample(yCbCr);

        //I3.  Inverse Color space transformation and Supersampling

        workImg.reverseSampling(subYCbCr, yCbCr);




        //I3. Inverse Color space transformation and Supersampling

        //F3. Discrete Cosine Transform
        //I2. Inverse DCT

        //F4. Quantization
        //I1. De-quantization

        //F5. Compression Ratio
        input.next();
    }
}
