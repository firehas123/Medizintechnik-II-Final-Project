import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class Task_4_Filters implements PlugInFilter {
    // Sobel, Scharr, and Prewitt kernels as int arrays
    protected int[][] SobelX = {{1, 0, -1}, {2, 0, -2}, {1, 0, -1}};
    protected int[][] SobelY = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};

    protected int[][] ScharrX = {{3, 0, -3}, {10, 0, -10}, {3, 0, -3}};
    protected int[][] ScharrY = {{3, 10, 3}, {0, 0, 0}, {-3, -10, -3}};

    protected int[][] PrewittX = {{1, 0, -1}, {1, 0, -1}, {1, 0, -1}};
    protected int[][] PrewittY = {{1, 1, 1}, {0, 0, 0}, {-1, -1, -1}};

    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_8G;  // Indicates the plugin works on 8-bit grayscale images
    }

    @Override
    public void run(ImageProcessor imageProcessor) {
        GenericDialog gd = new GenericDialog("Select Edge Detection Filter");
        String[] filters = {"Sobel", "Scharr", "Prewitt"};
        gd.addChoice("Filter:", filters, filters[0]);
        gd.showDialog();
        if (gd.wasCanceled()) {
            return;  // Terminate the plugin if canceled
        }
        String selectedFilter = gd.getNextChoice();
        FloatProcessor fp = imageProcessor.convertToFloatProcessor();
        int[][] selectedKernelX = null;
        int[][] selectedKernelY = null;

        switch (selectedFilter) {
            case "Sobel":
                selectedKernelX = SobelX;
                selectedKernelY = SobelY;
                break;
            case "Scharr":
                selectedKernelX = ScharrX;
                selectedKernelY = ScharrY;
                break;
            case "Prewitt":
                selectedKernelX = PrewittX;
                selectedKernelY = PrewittY;
                break;
        }

        FloatProcessor resultX = applyFilter(fp, selectedKernelX);
        FloatProcessor resultY = applyFilter(fp, selectedKernelY);
        FloatProcessor gradient = getGradient(resultX, resultY);

        new ImagePlus(selectedFilter + " Edge Detection", gradient).show();
    }

    // Manual convolution as described
    public FloatProcessor applyFilter(FloatProcessor in, int[][] kernel) {
        int width = in.getWidth();
        int height = in.getHeight();
        FloatProcessor out = new FloatProcessor(width, height);

        // Apply convolution (3x3 kernel)
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float sum = 0;
                for (int ky = 0; ky < 3; ky++) {
                    for (int kx = 0; kx < 3; kx++) {
                        int pixelX = x + kx - 1;
                        int pixelY = y + ky - 1;
                        float pixelValue = in.getf(pixelX, pixelY);
                        sum += (kernel[ky][kx] * pixelValue);
                    }
                }
                out.setf(x, y, sum);
            }
        }

        // Copy border pixels from the original image to the output
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y == 0 || y == height - 1 || x == 0 || x == width - 1) {
                    out.setf(x, y, in.getf(x, y));
                }
            }
        }

        return out;
    }


    // Calculating the gradient magnitude
    public FloatProcessor getGradient(FloatProcessor inX, FloatProcessor inY) {
        int width = inX.getWidth();
        int height = inX.getHeight();
        if (width != inY.getWidth() || height != inY.getHeight()) {
            throw new IllegalArgumentException("Input images must have the same dimensions");
        }

        FloatProcessor gradient = new FloatProcessor(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float gx = inX.getf(x, y);
                float gy = inY.getf(x, y);
                float grad = (float) Math.sqrt((gx * gx + gy * gy));
                gradient.setf(x, y, grad);
            }
        }
        return gradient;
    }
}
