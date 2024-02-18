// CSI2120 Project Part 1
// By Kaitlyn Miltimore, 300067827
// References:
// https://www.scaler.com/topics/sort-map-by-value-in-java/
// https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimilaritySearch {

    public static void main(String[] args) {
        // Set d = 3 to convert to 3-bit
        int d = 3;

        // Check if input is correct
        if (args.length != 2) {
            System.out.println("Usage: java SimilaritySearch <query_image_filename> <image_dataset_directory>");
            return;
        }

        // Parse input
        String queryImageFilename = "queryImages/" + args[0];
        String datasetDirectory = args[1];

        // Compute the histogram of the query image
        ColorImage queryImage = new ColorImage(queryImageFilename);
        ColorHistogram queryHistogram = new ColorHistogram(d);
        queryHistogram.setImage(queryImage);

        // Uncomment to view image conversion to histogram
        // queryHistogram.saveColorHistogram(queryImageFilename+".txt");

        // Load and process histograms from the dataset
        File imageDataset = new File(datasetDirectory);
        File[] datasetFiles = imageDataset.listFiles();
        if (datasetFiles != null) {
            // Map to store similarity scores and filenames
            Map<String, Double> similarityMap = new HashMap<>();

            // Get dataset histogram and compare with query one
            for (File file : datasetFiles) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    // Generate ColorHistogram instance for every txt file in dataset
                    ColorHistogram datasetHistogram = new ColorHistogram(file.getAbsolutePath());

                    // Compute similarity between query and dataset histograms
                    double intersection = queryHistogram.compare(datasetHistogram);

                    // Store filename and similarity
                    similarityMap.put(file.getName(), intersection);
                }
            }

            // Sort similarity list based on similarity
            List<Map.Entry<String, Double>> sortedList = new ArrayList<>(similarityMap.entrySet());
            sortedList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

            // Print the top 5 most similar images
            System.out.println("5 most similar images to " + queryImageFilename + ":");
            int count = 0;
            DecimalFormat df = new DecimalFormat("#.##"); // Format similarity to two decimal points
            for (Map.Entry<String, Double> entry : sortedList) {
                // Multiply intersection by 100 to express as a percentage
                double printSimilarity = entry.getValue()*100;
                System.out.println((count + 1) + ". " + entry.getKey() + " -> Similarity is " + df.format(printSimilarity) + "%");
                count++;
                // Break after getting the top 5 images
                if (count == 5) {
                    break;
                }
            }
        } else {
            System.out.println("No files found in the dataset directory.");
        }
    }
}




