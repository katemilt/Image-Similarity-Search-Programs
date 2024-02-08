// CSI2120 Project Part 1
// By Kaitlyn Miltimore, 300067827

import java.lang.Math;

public class ColorHistogram {

    // Instance variables
    private int[] colorHisto;
    
    public ColorHistogram(int d) {
        // Constructor that constructs ColorHistogram instance for d-bit image
        // Number of bins in the histogram will be N=2^(D*3)
        int numBins = (int) Math.pow(2, (d*3));
        colorHisto = new int[numBins];
    }

    public ColorHistogram(String filename) {
        // Constructor that constructs ColorHistogram from text file
    }

    public void setImage(ColorImage image) {
        // Associates an image with a histogram instance
        // Index of histogram bin with color [R',G',B']: (R' << (2 * D)) + (G' << D) + B)
        image.reduceColor(d);

    }

    public double[] getHistogram() {
        // Returns the normalized histogram of the image
    }

    public double compare(ColorHistogram hist) {
        // Returns the intersection between two histograms
        double intersection = 0;
        for (int i = 0; i < colorHisto.length; i++) {
            intersection += Math.min(colorHisto[i], hist.colorHisto[i]);
        }
        return intersection;
    }

    public void ColorHistogram (String filename) {
        // Saves the histogram into a text file
    }
}
