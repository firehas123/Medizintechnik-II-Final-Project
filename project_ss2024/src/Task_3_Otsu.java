//import ij.ImagePlus;
//import ij.plugin.filter.PlugInFilter;
//import ij.process.ByteProcessor;
//import ij.process.ImageProcessor;
//
//import java.util.Arrays;
//
//public class Task_3_Otsu implements PlugInFilter {
//    int NUM_INTENSITY_LEVELS = 256;
//
//    @Override
//    public int setup(String s, ImagePlus imagePlus) {
//        return DOES_8G;
//    }
//
//    @Override
//    public void run(ImageProcessor imageProcessor) {
//        int threshold = otsuGetThreshold(imageProcessor);
//
//        ByteProcessor result = otsuSegementation(imageProcessor, threshold);
//
//        ImagePlus resultImage = new ImagePlus("Otsu Segmentation", result);
//        resultImage.show();
//
//        System.out.println("Otsu Optimal Threshold: " + threshold);
//    }
//
//    public ByteProcessor otsuSegementation(ImageProcessor ip, int threshold) {
//
//        Task_1_Threshold Threshold = new Task_1_Threshold();
//        ByteProcessor illuminatedImg = Threshold.correctIllumination(ip);
//
//        return Threshold.threshold(illuminatedImg, threshold);
//    }
//
//    public double[] getHistogram(ImageProcessor in) {
//
//        double[] imageHistogram = new double[NUM_INTENSITY_LEVELS];
//
//        int imageWidth = in.getWidth();
//        int imageHeight = in.getHeight();
//        double totalNoOfPixels = imageWidth * imageHeight;
//
//        // updating corresponding historgram entry
//        for (int y = 0; y < imageHeight; y++) {
//            for (int x = 0; x < imageWidth; x++) {
//                int pixel = in.getPixel(x, y);
//                imageHistogram[pixel]++;
//            }
//        }
//
//        // normalizing historgam to create probability distribution
//        for (int x = 0; x < NUM_INTENSITY_LEVELS; x++) {
//            imageHistogram[x] = imageHistogram[x] / totalNoOfPixels;
//        }
//
//        return imageHistogram;
//    }
//
//    public double[] getP1(double[] histogram) {
//        double[] P1 = new double[histogram.length];
//
//        P1[0] = histogram[0];
//        for (int i = 1; i < histogram.length; i++) {
//            P1[i] = P1[i - 1] + histogram[i];
//        }
//
//        return P1;
//    }
//
//    public double[] getP2(double[] P1) {
//        double[] P2 = new double[P1.length];
//
//        for (int i = 0; i < P1.length; i++) {
//            P2[i] = 1 - P1[i];
//        }
//        return P2;
//    }
//
//    public double[] getMu1(double[] histogram, double[] P1) {
//        double[] mu1 = new double[histogram.length];
//        double sum = 0;
//
//        for (int i = 0; i < P1.length; i++) {
//            sum += (i+1) * histogram[i];  // Accumulation = (i+1) * h(i)
//            if (P1[i] > 0) {
//                mu1[i] = sum / P1[i];
//            } else {
//                mu1[i] = sum / (10e-10);  // Avoid division by zero
//            }
//        }
//
//        return mu1;
//    }
//
//    ;
//
//    public double[] getMu2(double[] histogram, double[] P2) {
//        double[] mu2 = new double[histogram.length];
//        double sum = 0;
//
//        for(int i = 0; i < histogram.length; i++) {
//            sum += (i+1) * histogram[i];  // Accumulation = (i+1) * h(i)
//            if (P2[i] > 0) {
//                mu2[i] = sum / P2[i];
//            } else {
//                mu2[i] = sum / (10e-10);  // Avoid division by zero
//            }
//        }
//
//        return mu2;
//    }
//
//    public double[] getSigmas(double[] histogram, double[] P1, double[] P2, double[] mu1, double[] mu2) {
//        double[] sigma = new double[P1.length];
//
//        for (int i = 0; i < histogram.length; i++) {
//            double muDifferenceSquared = (mu1[i] - mu2[i]) * (mu1[i] - mu2[i]);
//            sigma[i] = P1[i] * P2[i] * muDifferenceSquared;
//        }
//        return sigma;
//    };
//
//    public int getMaximum(double[] sigmas){
//        double maxSigma = sigmas[0];
//        int maxSigmaIndex = 0;
//
//        for (int i = 1; i < sigmas.length; i++) {
//            if (sigmas[i] > maxSigma) {
//                maxSigma = sigmas[i];
//                maxSigmaIndex = i;
//            }
//        }
//
//        return maxSigmaIndex;
//    };
//
//    public int otsuGetThreshold(ImageProcessor in) {
//        double [] histogram = getHistogram(in);
//        double [] P1 = getP1(histogram);
//        double [] P2 = getP2(histogram);
//        double [] Mu1 = getMu1(histogram, P1);
//        double [] Mu2 = getMu2(histogram, P2);
//        double [] sigmas = getSigmas(histogram,P1,P2,Mu1,Mu2);
//
//        int otsuThreshold = getMaximum(sigmas);
//
//        return otsuThreshold;
//    }
//}

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Task_3_Otsu implements PlugInFilter {
    int NUM_INTENSITY_LEVELS = 256;

    Task_1_Threshold thresholdObj = new Task_1_Threshold();  // Inherit methods from Task_1

    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_8G;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {
        // Step 2: Calculate the optimal threshold using Otsu's method
        int optimalThreshold = otsuGetThreshold(imageProcessor);

        // Step 3: Apply the optimal threshold to segment the image
        ByteProcessor thresholdedIp = otsuSegmentation(imageProcessor, optimalThreshold);

        // Step 4: Display the result
        ImagePlus thresholdedImage = new ImagePlus("Otsu Segmented Image", thresholdedIp);
        thresholdedImage.show();

        // Step 5: Print the threshold value to the terminal
        System.out.println("Otsu's optimal threshold: " + optimalThreshold);
    }

    // Method to calculate the optimal threshold using Otsu's method
    public int otsuGetThreshold(ImageProcessor ip) {
        double[] histogram = getHistogram(ip);
        double[] P1 = getP1(histogram);
        double[] P2 = getP2(P1);
        double[] mu1 = getMu1(histogram, P1);
        double[] mu2 = getMu2(histogram, P2);
        double[] sigmas = getSigmas(histogram, P1, P2, mu1, mu2);

        return getMaximum(sigmas);
    }

    // Method to apply the calculated threshold to segment the image
    public ByteProcessor otsuSegmentation(ImageProcessor imageProcessor, int threshold) {
        // Step 1: Correct the illumination
        ByteProcessor ip = thresholdObj.correctIllumination(imageProcessor);
        return thresholdObj.threshold(ip, threshold);
    }

    // Generate the histogram of the image and normalize it
    public double[] getHistogram(ImageProcessor ip) {
        double[] histogram = new double[NUM_INTENSITY_LEVELS];
        int totalPixels = ip.getWidth() * ip.getHeight();

        // Generate the histogram
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                int pixel = ip.getPixel(x, y);
                histogram[pixel]++;
            }
        }

        // Normalize the histogram
        for (int i = 0; i < histogram.length; i++) {
            histogram[i] /= totalPixels;
        }

        return histogram;
    }

    // Calculate cumulative probabilities P1(θ)
    public double[] getP1(double[] histogram) {
        double[] P1 = new double[NUM_INTENSITY_LEVELS];
        P1[0] = histogram[0];
        for (int i = 1; i < NUM_INTENSITY_LEVELS; i++) {
            P1[i] = P1[i - 1] + histogram[i];
        }
        return P1;
    }

    // Calculate cumulative probabilities P2(θ) based on P1(θ)
    public double[] getP2(double[] P1) {
        double[] P2 = new double[NUM_INTENSITY_LEVELS];
        for (int i = 0; i < NUM_INTENSITY_LEVELS; i++) {
            P2[i] = 1 - P1[i];
        }
        return P2;
    }

    // Calculate mean values μ1(θ) for the background class
//    public double[] getMu1(double[] histogram, double[] P1) {
//        double[] mu1 = new double[NUM_INTENSITY_LEVELS];
//        double cumulativeSum = 0;
//        double denominator = 0;
//        for (int i = 0; i < NUM_INTENSITY_LEVELS; i++) {
//            cumulativeSum += (i+1) * histogram[i];
//            denominator = (P1[i] > 0 ? P1[i] : 10e-10);
//            mu1[i] = cumulativeSum / denominator; // Avoid division by zero
//        }
//        return mu1;
//    }
//
//    // Calculate mean values μ2(θ) for the foreground class
//    public double[] getMu2(double[] histogram, double[] P2) {
//        double[] mu2 = new double[NUM_INTENSITY_LEVELS];
//        double cumulativeSum = 0;
//        double denominator = 0;
//        for (int i = 1; i < NUM_INTENSITY_LEVELS; i++) {
//            cumulativeSum += (i+1) * histogram[i];
//            denominator = (P2[i] > 0 ? P2[i] : 10e-10);
//            mu2[i] = cumulativeSum / denominator; // Avoid division by zero
//        }
//        return mu2;
//    }

    public double[] getMu1(double[] histogram, double[] P1){
        double [] Mu1 = new double[NUM_INTENSITY_LEVELS];
        double denominator = 0;
        for (int i = 0; i < NUM_INTENSITY_LEVELS; i++) {
            double sum = 0;
            for (int j = 0; j <= i; j++) {
                sum += (j+1) * histogram[j];
            }
            denominator = (P1[i] > 0 ? P1[i] : 10e-10);
            Mu1[i] = sum / denominator; // Avoid division by zero
            //Mu1[i] = sum / P1[i];
        }
        return Mu1;

    }
    public double[] getMu2(double[] histogram, double[] P2){
        double [] Mu2 = new double[NUM_INTENSITY_LEVELS];
        double denominator = 0;
        for (int i = 0; i < NUM_INTENSITY_LEVELS; i++) {
            double sum = 0;
            for (int j = i+1; j < NUM_INTENSITY_LEVELS; j++) {
                sum += (j+1) * histogram[j];
            }
            denominator = (P2[i] > 0 ? P2[i] : 10e-10);
            Mu2[i] = sum / denominator;
            //Mu2[i] = sum / P2[i];
        }
        return Mu2;
    }

    // Calculate between-class variance σB² (θ) for each possible threshold
    public double[] getSigmas(double[] histogram, double[] P1, double[] P2, double[] mu1, double[] mu2) {
        double[] sigmas = new double[NUM_INTENSITY_LEVELS];
        for (int i = 0; i < histogram.length; i++) {
            double sigmaB = P1[i] * P2[i] * Math.pow(mu1[i] - mu2[i], 2);
            sigmas[i] = sigmaB;
        }
        return sigmas;
    }

    // Find the threshold that corresponds to the maximum between-class variance
    public int getMaximum(double[] sigmas) {
        int maxIndex = 0;
        double maxValue = sigmas[0];
        for (int i = 1; i < sigmas.length; i++) {
            if (sigmas[i] > maxValue) {
                maxValue = sigmas[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
