// CSI2120 Project Part 1
// By Kaitlyn Miltimore, 300067827
// References:
// https://stackoverflow.com/questions/41783414/get-bits-per-pixel-of-a-bufferedimage
// https://stackoverflow.com/questions/16698372/isolating-red-green-blue-channel-in-java-bufferedimage
// https://stackoverflow.com/questions/11951646/setrgb-in-java

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;

public class ColorImage {

    // Instance variables
    private BufferedImage image;
    private int width;
    private int height;
    private int depth;
    
    public ColorImage(String filename) {
        // Constructor that creates an image from a jpg file
        try {
            File file = new File(filename);
            // Having issues with just using imageIO so adding in imageinputstream
            ImageInputStream iis = ImageIO.createImageInputStream(file);
            if (iis != null) {
                image = ImageIO.read(iis);
                // Determine width, height, and depth
                width = image.getWidth();
                height = image.getHeight();
                ColorModel colorModel = image.getColorModel();
                int pixelSize = colorModel.getPixelSize();
                depth = pixelSize;
            } else {
                System.out.println("Failed to create ImageInputStream.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public int[] getPixel(int i, int j) {
        // Returns 3-channel value of pixel at column i row j in form of 3-element array
        int[] pixel = new int[3];

        int imageRGB = image.getRGB(i, j);
        pixel[0] = (imageRGB >> 16) & 0xFF; // Isolate red colour by bitshift 16 to right
        pixel[1] = (imageRGB >> 8) & 0xFF;  // Isolate green colour by bitshift 8 to right
        pixel[2] = imageRGB & 0xFF;         // Isolate blue colour

        return pixel;
    }

    public void reduceColor(int d) {
        // Reduces the color space to a d-bit representation
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int[] pixel = getPixel(i, j);

                // Reduce pixel values by applying (8-D) right bit shifts for each channel 
                pixel[0] = (pixel[0] >> (8-d));
                pixel[1] = (pixel[1] >> (8-d));
                pixel[2] = (pixel[2] >> (8-d));

                // Combine individual colour values into single int using bitwise OR
                int newRGB = (pixel[0] << 16) | (pixel[1] << 8) | pixel[2];
                // Use setRGB function to update image values
                image.setRGB(i, j, newRGB);
            }
        }
    }

}
