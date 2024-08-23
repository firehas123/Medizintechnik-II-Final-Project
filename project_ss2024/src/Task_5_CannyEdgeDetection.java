import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class Task_5_CannyEdgeDetection implements PlugInFilter {

    private final int[][] SobelX = {{1, 0, -1}, {2, 0, -2}, {1, 0, -1}};
    private final int[][] SobelY = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};

    @Override
    public void run(ImageProcessor imageProcessor) {
        // Show dialog to get parameters from user
        GenericDialog gd = new GenericDialog("Canny Edge Detection");
        gd.addNumericField("Sigma (Gaussian Blur):", 1.0, 1);
        gd.addNumericField("Upper Threshold (%):", 15, 0);
        gd.addNumericField("Lower Threshold (%):", 5, 0);
        gd.showDialog();
        if (gd.wasCanceled()) return;

        double sigma = gd.getNextNumber();
        int upper = (int) gd.getNextNumber();
        int lower = (int) gd.getNextNumber();

        // Apply Gaussian Blur
        FloatProcessor blurredImage = (FloatProcessor) imageProcessor.convertToFloatProcessor();
        blurredImage.blurGaussian(sigma);

        // Create gradient and direction processors
        FloatProcessor gradientX = applyFilter(blurredImage, SobelX);
        FloatProcessor gradientY = applyFilter(blurredImage, SobelY);
        FloatProcessor gradientMagnitude = getGradient(gradientX, gradientY);
        ByteProcessor direction = getDir(gradientX, gradientY);

        // Apply Non-Maximum Suppression
        FloatProcessor suppressed = nonMaxSuppress(gradientMagnitude, direction);

        // Apply Hysteresis Thresholding
        ByteProcessor edges = hysteresisThreshold(suppressed, upper, lower);

        // Display the final edge-detected image
        new ImagePlus("Canny Edge Detection", edges).show();
    }

    public FloatProcessor applyFilter(FloatProcessor in, int[][] kernel) {
        int width = in.getWidth();
        int height = in.getHeight();
        FloatProcessor out = new FloatProcessor(width, height);

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float sum = 0;
                for (int ky = 0; ky < 3; ky++) {
                    for (int kx = 0; kx < 3; kx++) {
                        int pixelX = x + kx - 1;
                        int pixelY = y + ky - 1;
                        float pixelValue = in.getf(pixelX, pixelY);
                        sum += kernel[ky][kx] * pixelValue;
                    }
                }
                out.setf(x, y, sum);
            }
        }
        return out;
    }

    public FloatProcessor getGradient(FloatProcessor inX, FloatProcessor inY) {
        int width = inX.getWidth();
        int height = inX.getHeight();
        FloatProcessor gradient = new FloatProcessor(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float gx = inX.getf(x, y);
                float gy = inY.getf(x, y);
                float grad = (float) Math.sqrt(gx * gx + gy * gy);
                gradient.setf(x, y, grad);
            }
        }
        return gradient;
    }

    public ByteProcessor getDir(FloatProcessor X_Deriv, FloatProcessor Y_Deriv) {
        int width = X_Deriv.getWidth();
        int height = X_Deriv.getHeight();
        ByteProcessor direction = new ByteProcessor(width, height);

        // Possible angle directions in degrees
        int[] angles = {0, 45, 90, 135, 180};

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float gx = X_Deriv.getf(x, y);
                float gy = Y_Deriv.getf(x, y);

                // Calculate the direction in degrees
                float angle = (float) Math.toDegrees(Math.atan2(gy, gx));
                if (angle < 0) {
                    angle += 180;  // Map negative angles to positive equivalents
                }

                // Find the closest angle from the angles array
                int closestAngle = 0;
                float minDiff = Float.MAX_VALUE;
                for (int i = 0; i < angles.length; i++) {
                    float diff = Math.abs(angle - angles[i]);
                    if (diff < minDiff) {
                        minDiff = diff;
                        closestAngle = angles[i];
                    }
                }

                // Map 180° back to 0°
                if (closestAngle == 180) {
                    closestAngle = 0;
                }

                // Store the direction in the ByteProcessor
                direction.set(x, y, closestAngle);
            }
        }

        return direction;
    }

    public FloatProcessor nonMaxSuppress(FloatProcessor Grad, ByteProcessor Dir) {
        int width = Grad.getWidth();
        int height = Grad.getHeight();
        FloatProcessor output = new FloatProcessor(width, height);

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int direction = Dir.get(x, y);
                float gradValue = Grad.getf(x, y);
                float neighbor1 = 0;
                float neighbor2 = 0;

                // Determine the two neighboring pixels along the gradient direction
                switch (direction) {
                    case 0:  // 0 degrees (horizontal)
                        neighbor1 = Grad.getf(x - 1, y);
                        neighbor2 = Grad.getf(x + 1, y);
                        break;
                    case 45:  // 45 degrees (diagonal: top-left to bottom-right)
                        neighbor1 = Grad.getf(x - 1, y - 1);
                        neighbor2 = Grad.getf(x + 1, y + 1);
                        break;
                    case 90:  // 90 degrees (vertical)
                        neighbor1 = Grad.getf(x, y - 1);
                        neighbor2 = Grad.getf(x, y + 1);
                        break;
                    case 135:  // 135 degrees (diagonal: top-right to bottom-left)
                        neighbor1 = Grad.getf(x + 1, y - 1);
                        neighbor2 = Grad.getf(x - 1, y + 1);
                        break;
                }

                // Keep the pixel value if it's a local maximum
                if (gradValue >= neighbor1 && gradValue >= neighbor2) {
                    output.setf(x, y, gradValue);
                } else {
                    output.setf(x, y, 0);
                }
            }
        }

        return output;
    }

    public ByteProcessor hysteresisThreshold(FloatProcessor In, int upper, int lower) {
        // Calculate threshold values based on input percentages
        float tHigh = ((float) In.getMax() * upper) / 100f;
        float tLow = ((float) In.getMax() * lower) / 100f;

        int width = In.getWidth();
        int height = In.getHeight();
        ByteProcessor Out = new ByteProcessor(width, height);

        // First pass: Mark strong edges
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float pixelValue = In.getf(x, y);
                if (pixelValue >= tHigh) {
                    Out.set(x, y, 255); // Strong edge
                } else if (pixelValue >= tLow) {
                    Out.set(x, y, 128); // Weak edge (marked as 128 temporarily)
                } else {
                    Out.set(x, y, 0); // Non-edge
                }
            }
        }

        // Second pass: Connect weak edges to strong edges
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (Out.get(x, y) == 128 && hasNeighbours(Out, x, y)) {
                        Out.set(x, y, 255); // Connect weak edge to strong edge
                        changed = true;
                    }
                }
            }
        }

        // Final pass: Remove any remaining weak edges
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (Out.get(x, y) == 128) {
                    Out.set(x, y, 0); // Remove isolated weak edge
                }
            }
        }

        return Out;
    }

    public boolean hasNeighbours(ByteProcessor BP, int x, int y) {
        int count = (BP.getPixel(x + 1, y) + BP.getPixel(x - 1, y) + BP.getPixel(x, y + 1) + BP.getPixel(x, y - 1) +
                BP.getPixel(x + 1, y + 1) + BP.getPixel(x - 1, y - 1) + BP.getPixel(x - 1, y + 1) + BP.getPixel(x + 1, y - 1));
        count /= 255;
        return (count > 0);
    }

    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_8G;
    }

}