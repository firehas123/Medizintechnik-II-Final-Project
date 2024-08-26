import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.IJ;

public class Task_2_EvaluateSegmentation implements PlugInFilter {

    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_8G;
    }

    @Override
    public void run(ImageProcessor ip) {
        try {
            // Prompt the user to open a reference image
            IJ.showMessage("Select Reference Image", "Please select the reference image for comparison.");
            ImagePlus referenceImage = IJ.openImage();

            // Check if the reference image was loaded successfully
            if (referenceImage == null) {
                throw new IllegalArgumentException("No reference image selected.");
            }

            ImageProcessor reference = referenceImage.getProcessor();

//            // Debug: Print out some information about the images
//            System.out.println("Segmentation Image - Width: " + ip.getWidth() + ", Height: " + ip.getHeight());
//            System.out.println("Reference Image - Width: " + reference.getWidth() + ", Height: " + reference.getHeight());
//
//            // Print out some pixel values for comparison
//            System.out.println("Segmentation Pixel (0,0): " + ip.getPixel(0, 0));
//            System.out.println("Reference Pixel (0,0): " + reference.getPixel(0, 0));

            // Evaluate segmentation
            EvaluationResult result = evaluateSegmentation(ip, reference);

            if (result != null) {
                // Output the results
                IJ.showMessage("Segmentation Evaluation Results",
                        "Sensitivity: " + result.getSensitivity() + "\n" +
                                "Specificity: " + result.getSpecificity());
            } else {
                IJ.error("Error", "The images do not have the same dimensions.");
            }
        } catch (Exception e) {
            IJ.error("Error", e.getMessage());
        }
    }


    private EvaluationResult evaluateSegmentation(ImageProcessor segmentation, ImageProcessor reference) {
        // Check if both images have the same dimensions
        if (segmentation.getWidth() != reference.getWidth() || segmentation.getHeight() != reference.getHeight()) {
            return null; // Return null if dimensions do not match
        }

        int tp = 0; // true positives
        int tn = 0; // true negatives
        int fp = 0; // false positives
        int fn = 0; // false negatives

        int width = segmentation.getWidth();
        int height = segmentation.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int segPixel = segmentation.getPixel(x, y);
                int refPixel = reference.getPixel(x, y);

                if (segPixel == 255 && refPixel == 255) {
                    tp++;
                } else if (segPixel == 0 && refPixel == 0) {
                    tn++;
                } else if (segPixel == 255 && refPixel == 0) {
                    fp++;
                } else if (segPixel == 0 && refPixel == 255) {
                    fn++;
                }
            }
        }

        // Calculate sensitivity and specificity
        double sensitivity = tp / (double) (tp + fn);
        double specificity = tn / (double) (tn + fp);

        // Return the result
        return new EvaluationResult(specificity, sensitivity);
    }
}
