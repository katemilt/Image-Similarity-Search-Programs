// CSI2120 Project Part 1
// By Kaitlyn Miltimore, 300067827

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimilaritySearch {

    public static void main(String[] args) {
        // Check if correct number of arguments are provided
        int d = 3;

        if (args.length != 2) {
            System.out.println("Usage: java SimilaritySearch <query_image_filename> <image_dataset_directory>");
            return;
        }

        // Parse command line arguments
        String queryImageFilename = "queryImages/" + args[0];
        String datasetDirectory = args[1];

        // Load query image and compute its histogram
        ColorImage queryImage = new ColorImage(queryImageFilename);
        ColorHistogram queryHistogram = new ColorHistogram(d);
        queryHistogram.setImage(queryImage);
    
        System.out.println(queryImage.getDepth());
        System.out.println(queryImage.getHeight());
        System.out.println(queryImage.getWidth());
        //queryHistogram.saveColorHistogram(queryImageFilename+".txt");

        // Load and process images from the dataset
        File datasetDir = new File(datasetDirectory);
        File[] datasetFiles = datasetDir.listFiles();
        if (datasetFiles != null) {
            // List to store similarity scores and filenames
            List<ImageSimilarity> similarityList = new ArrayList<>();

            for (File file : datasetFiles) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    // Load image from the dataset and compute its histogram
                    // ColorImage datasetImage = new ColorImage(file.getAbsolutePath());
                    ColorHistogram datasetHistogram = new ColorHistogram(file.getAbsolutePath());
                    //datasetHistogram.setImage(datasetImage);

                    // Compute similarity between query and dataset histograms
                    double similarity = queryHistogram.compare(datasetHistogram);

                    // Store filename and similarity score
                    similarityList.add(new ImageSimilarity(file.getName(), similarity));
                }
            }

            // Sort similarity list based on similarity score
            Collections.sort(similarityList, Comparator.reverseOrder());

            // Print the top 5 most similar images
            System.out.println("Top 5 most similar images to " + queryImageFilename + ":");
            for (int i = 0; i < Math.min(5, similarityList.size()); i++) {
                ImageSimilarity imageSimilarity = similarityList.get(i);
                System.out.println((i + 1) + ". " + imageSimilarity.getFilename() + " - Similarity: " + imageSimilarity.getSimilarity());
            }
        } else {
            System.out.println("No files found in the dataset directory.");
        }
    }

    static class ImageSimilarity implements Comparable<ImageSimilarity> {
        private String filename;
        private double similarity;

        public ImageSimilarity(String filename, double similarity) {
            this.filename = filename;
            this.similarity = similarity;
        }

        public String getFilename() {
            return filename;
        }

        public double getSimilarity() {
            return similarity;
        }

        @Override
        public int compareTo(ImageSimilarity other) {
            return Double.compare(this.similarity, other.similarity);
        }
    }
}




