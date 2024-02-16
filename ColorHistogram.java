// CSI2120 Project Part 1
// By Kaitlyn Miltimore, 300067827

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

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
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                if (lineNumber == 0) {
                    // Parse the first line to get the maximum frequency
                    int max = Integer.parseInt(line.trim());
                    // Initialize the colorHisto array with the correct size
                    colorHisto = new int[max];
                } else {
                    // Parse histogram data from subsequent lines
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

                int histoIndex = (pixel[0] << (2 * d)) + (pixel[1] << d) + pixel[2];

                colorHisto[histoIndex]++;
            }
        }
    }

    public double[] getHistogram() {
        // Returns the normalized histogram of the image
        // Normalize h such that the values of all bins sum to 1.0
        int max = colorHisto[0];
        for (int i = 0; i < colorHisto.length; i++) {
            if (colorHisto[i] > max) 
                max = colorHisto[i];
        }
        // If == 0 then histogram is empty/null
        if (max == 0)
            System.out.println("AH");
    
        double[] normalizedHisto = new double[colorHisto.length];
        // Ensure all bins are summed to 1.0
        double normFactor = 1.0 / max;
        for (int i = 0; i < colorHisto.length; i++) {
            // Calculate all values of normalized histogram using normFactor
            normalizedHisto[i] = normFactor * colorHisto[i];
        }
        return normalizedHisto;
    }

    public double compare(ColorHistogram hist) {
        // Returns the intersection between two histograms
        // Intersection can be computed as: sum over I(min(H1(I), H2(I)))
        double[] hist1 = this.getHistogram();
        double[] hist2 = hist.getHistogram();
        double intersection = 0;
        for (int i = 0; i < hist1.length; i++) {
            intersection += Math.min(hist1[i], hist2[i]);
        }
        double maxSimilarity = hist1.length; // Maximum possible similarity value
        return intersection / maxSimilarity;
        //return intersection;

        // double intersection = 0;
        // for (int i = 0; i < colorHisto.length; i++) {
        //     intersection += Math.min(colorHisto[i], hist.colorHisto[i]);
        // }
        // return intersection;
    }

    public void saveColorHistogram (String filename) {
        // Saves the histogram into a text file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename+".txt"))) {
            for (int value : colorHisto) {
                bw.write(Integer.toString(value));
                bw.newLine(); // Add a new line after each value
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
