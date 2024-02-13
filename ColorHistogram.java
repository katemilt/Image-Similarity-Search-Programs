// CSI2120 Project Part 1
// By Kaitlyn Miltimore, 300067827

import java.lang.Math;

public class ColorHistogram {

    // Instance variables
    private int[] colorHisto;
    private int d;
    
    public ColorHistogram(int d) {
        // Constructor that constructs ColorHistogram instance for d-bit image
        // Number of bins in the histogram will be N=2^(D*3)
        this.d = d;
        int numBins = (int) Math.pow(2, (d*3));
        this.colorHisto = new int[numBins];
    }

    public ColorHistogram(String filename) {
        // Constructor that constructs ColorHistogram from text file
    }

    public void setImage(ColorImage image) {
        // Associates an image with a histogram instance
        // Index of histogram bin with color [R',G',B']: (R' << (2 * D)) + (G' << D) + B)
        image.reduceColor(d);

        for(int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                int [] pixel = image.getPixel(i, j);

                int histoIndex = (pixel[0] << (2 * d)) + (pixel[1] << d) + pixel[2];

                colorHisto[histoIndex]++;
            }
        }
    }

    public double[] getHistogram() {
        // Returns the normalized histogram of the image
        // Normalize h such that the values of all bins sum to 1.0
        int max = colorHisto[0];
        for(int i = 0; i < colorHisto.length; i++) {
            if(colorHisto[i] > max) {
                max = colorHisto[i];
            }
        }
        // If == 0 then histogram is empty/null
        if(max == 0) {
            return null;
        }
        double [] normalizedHisto = new double[colorHisto.length];
        // Ensure all bins are summed to 1.0
        double normFactor = 1.0 / max;
        for(int i = 0; i < colorHisto.length; i++) {
            // Calculate all values of normalized histogram using normFactor
            normalizedHisto[i] = normFactor * colorHisto[i];
        }
        return normalizedHisto;
    }

    public double compare(ColorHistogram hist) {
        // Returns the intersection between two histograms
        // Intersection can be computed as: sum over I(min(H1(I), H2(I)))
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
