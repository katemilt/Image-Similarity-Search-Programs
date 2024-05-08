// CSI2120 Project Part 1
// By Kaitlyn Miltimore, 300067827

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
        for (int i = 0; i < numBins; i++) {
            this.colorHisto[i] = 0;
        }
    }

    public ColorHistogram(String filename) {
        // Constructor that constructs ColorHistogram from text file
        try (BufferedReader inputFile = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            while ((line = inputFile.readLine()) != null) {
                if (lineNumber == 0) {
                    // Parse first line to get max frequency
                    int max = Integer.parseInt(line.trim());
                    // Initialize array with correct size
                    colorHisto = new int[max];
                } else {
                    // Parse remainder of file
                    String[] values = line.trim().split("\\s+");
                    for (int i = 0; i < values.length; i++) {
                        colorHisto[i] = Integer.parseInt(values[i]);
                    }
                }
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setImage(ColorImage image) {
        // Associates an image with a histogram instance
        // Index of histogram bin with color [R',G',B']: (R' << (2 * D)) + (G' << D) + B)
        image.reduceColor(d);
        for(int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                int [] pixel = image.getPixel(i, j);
                // Use given formula to calculate index
                int histoIndex = (pixel[0] << (2 * d)) + (pixel[1] << d) + pixel[2];
                colorHisto[histoIndex]++;
            }
        }
    }

    public double[] getHistogram() {
        // Returns the normalized histogram of the image
        // Normalize h such that the values of all bins sum to 1.0
        double[] normalizedHisto = new double[colorHisto.length];
        int sum = 0;

        for(int i = 0; i < colorHisto.length; i++) {
            sum += colorHisto[i];
        }
        // Ensure all bins are summed to 1.0
        for(int i = 0; i < colorHisto.length; i++) {
            normalizedHisto[i] = (double) colorHisto[i] / sum;
        }
        // Return the normalized version of colorHisto
        return normalizedHisto;
    }

    public double compare(ColorHistogram hist) {
        // Returns the intersection between two histograms
        // Intersection can be computed as: sum over I(min(H1(I), H2(I)))
        // Normalize both the query image histogram and the dataset histogram
        double[] hist1 = this.getHistogram();
        double[] hist2 = hist.getHistogram();
        double intersection = 0;
        // Calculate the total intersection
        for (int i = 0; i < hist1.length; i++) {
            intersection += Math.min(hist1[i], hist2[i]);
        }

        return intersection;
    }

    public void saveColorHistogram (String filename) {
        // Saves the histogram into a text file
        try (BufferedWriter outputFile = new BufferedWriter(new FileWriter(filename+".txt"))) {
            for (int i = 0; i < colorHisto.length; i++) {
                outputFile.write(Integer.toString(colorHisto[i]));
                // Add a space after each value
                if (i < colorHisto.length - 1) {
                    outputFile.write(" ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
