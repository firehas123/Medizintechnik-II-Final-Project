//import ij.IJ; // for debugging
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.Blitter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.FloatProcessor;


public class Task_1_Threshold implements PlugInFilter {

    private ImageProcessor resultProcessor; // this is for userInterface to get the result as we aren't processing on the processed image

    public Task_1_Threshold() {
    }

    public int setup(String s, ImagePlus imagePlus) {
        return 1;
    }

    public void run(ImageProcessor ip) {
        ImageProcessor originalIp = ip.duplicate();
        GenericDialog gd = new GenericDialog("Thresholding");
        gd.addNumericField("Threshold value:", 128.0, 0);
        gd.addCheckbox("Correct uneven illumination", false);
        gd.showDialog();
        if (!gd.wasCanceled()) {
            int threshold = (int)gd.getNextNumber();
            boolean correct = gd.getNextBoolean();
//            IJ.log("Threshold: " + threshold);
//            IJ.log("Correct illumination: " + correct);
            ImageProcessor ipCopy;
            if (correct) {
                ipCopy = this.correctIllumination(originalIp);
            } else {
                ipCopy = originalIp;
            }

//            //Show the corrected image separately
//            ImagePlus correctedImage = new ImagePlus("Corrected Image", ipCopy);
//            correctedImage.show();

            ByteProcessor thresholdedIp = this.threshold(ipCopy, threshold);
            this.resultProcessor = thresholdedIp;// this is for userInterface to get the result as we aren't processing on the processed image
            ImagePlus thresholdedImage = new ImagePlus("Thresholded Image", thresholdedIp);
            thresholdedImage.show();
        }
    }

    public ByteProcessor correctIllumination(ImageProcessor ip) {
        // Convert the input image to a FloatProcessor
        FloatProcessor duplicateIp = ip.duplicate().convertToFloatProcessor();

        // Create a duplicate of the FloatProcessor for filtering
        FloatProcessor blurredIp = ip.duplicate().convertToFloatProcessor();

        // Apply Gaussian blur with sigma = 75
        double sigma = 75.0;
        blurredIp.blurGaussian(sigma);

        // Divide the original FloatProcessor by the blurred FloatProcessor by using the ImajeJ-API
        duplicateIp.copyBits(blurredIp, 0, 0, Blitter.DIVIDE);

        // Convert the result back to a ByteProcessor and return it
        return duplicateIp.convertToByteProcessor();
    }


    public ByteProcessor threshold(ImageProcessor ip, int threshold) {
        ByteProcessor bp = new ByteProcessor(ip.getWidth(), ip.getHeight());

        for(int y = 0; y < ip.getHeight(); ++y) {
            for(int x = 0; x < ip.getWidth(); ++x) {
                int pixel = ip.getPixel(x, y);
                bp.putPixel(x, y, pixel > threshold ? 255 : 0);
            }
        }

        return bp;
    }

    //Getter
    public ImageProcessor getResultProcessor() {
        return resultProcessor;
    }

}
