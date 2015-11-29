package chpt363p1;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import ij.ImageStack;
import ij.process.ImageProcessor;
import java.awt.Image;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * This class will contain the main contents for translating images into sound as described in the comments
 * of the methods
 * 
 * @author Chazz Young (0.0)
 * @version 0.0 - October 26, 2015
 * @version 1.0 - November 12, 2015
 */
public class AVIFrames
{
    private final AVI_Reader reader;
    private ImageStack imstack;
    
    public AVIFrames()
    {
        reader = new AVI_Reader();
        reader.run("");
        imstack = reader.getStack();     
    }
    
    /**
     * Get internal ImageProcessor image for manipulation
     * @param frame the frame number
     * @return the image as an ImageProcessor
     * #author Chazz Young(0.0)
     */
    private ImageProcessor getImage(int frame)
    {
        if(frame > getFrameCount()){
            System.err.println("Error, frame " + frame + " is larger than the number of frames.");
            return null;
        }
        ImageProcessor img = imstack.getProcessor(frame);
        img = img.resize(32, 32); // 32 by 32
        return img;
    }
    
    /**
     * Returns the AWT image to print  in the sample frame
     * @param frame the frame number 
     * @return The corresponding frame as an AWT image
     * @author Chazz Young(0.0)
     */
    public Image getImageToPrint(int frame)
    {
        if(frame < 1 || frame > getFrameCount()){return null;}
        return getImage(frame).createImage();
    }
    
    public int getFrameCount()
    {
        return imstack.getSize();
    }
    
    public int getRowHeight()
    {
        return getImage(1).getWidth();
    }
    
    public int getColumnHeight()
    {
        return getImage(1).getHeight();
    }
    
    /**
     * @return the colours from the center column of each frame with size
     * [frame number][center columns height]
     */
    public int[][] getVerticalSTI()
    {
        final int x = getFrameCount(); //x is t over time
        final int y = getColumnHeight(); //y is height of the column
        int[][] toReturn = new int[x][y];
        ImageProcessor currImg = getImage(1);
        int col = currImg.getWidth()/2;
        
        for(int i = 1; i < x; i++){//for each frame = 1 to end, intentional
            currImg = getImage(i);
            for(int j = 0; j < y; j++){//For each column pixel value
                toReturn[i - 1][j] = currImg.getPixel(col, j);
            }
        }
        return toReturn;
    }
    
    /**
     * @return the colours from the center row of each frame with size
     * [frame number][center columns height]
     */
    public int[][] getHorizontalSTI()
    {
        final int x = getFrameCount(); //x is t over time
        final int y = getRowHeight(); //y is height of the column
        int[][] toReturn = new int[x][y];
        ImageProcessor currImg = getImage(1);
        int row = currImg.getHeight()/2;
        
        for(int i = 1; i < x; i++){//for each frame = 1 to end, intentional
            currImg = getImage(i);
            for(int j = 0; j < y; j++){//For each row pixel value
                toReturn[i - 1][j] = currImg.getPixel(row, j);
            }
        }
        return toReturn;
    }
    
    /**
     * Taken from http://stackoverflow.com/questions/13391404/create-image-from-2d-color-array
     * @param pixels the 2d int array of pixels
     * @return the image representation of hte pixels
     */
    public BufferedImage pixels2img(int[][] pixels)
    {
        // Initialize Color[][] however you were already doing so.
        Color c;

        // Initialize BufferedImage, assuming Color[][] is already properly populated.
        BufferedImage bufferedImage = new BufferedImage(pixels.length, pixels[0].length,
        BufferedImage.TYPE_INT_RGB);

        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[x].length; y++) {
                c = new Color(pixels[x][y]);
                    bufferedImage.setRGB(x, y, c.getRGB());
            }
        }
        return bufferedImage;
    }
        
    /**
     * @param histogram
     * @param r
     * @param g
     * @return histogram of every frame
     * @author Brian Pak(1.0)
     */
    public int[][] fillHistogram(int[][] histogram, float r, float g)
    {
        int logbase2N = (int)(Math.log10(getRowHeight()) / Math.log10(2));
        
        // r and g are in the interval [0, 1]
        /* Added disambiguating brackets, otherwise the int typecast would only 
           apply to r and g*/
        int qr = (int)(r * logbase2N); // in the interval [0, 5]
        int qg = (int)(g * logbase2N); // in the interval [0, 5]
        
        // increment count
        histogram[qr][qg]++;
        
        return histogram;
    }
    
    public int getHistogramIntersection(int[][][] STIHistogram) {
        final int x = getRowHeight();
        final int y = getColumnHeight();
        final int z = getFrameCount();
        
        int I = 0;
        
        for (int k = 2; k < z; k++) {
            int[][] hist1 = STIHistogram[k - 1];
            int[][] hist2 = STIHistogram[k];
                    
            // for every row
            for (int i = 0; i < x; i++) {
                
                // for every column
                for (int j = 0; j < y; j++) {
                    if (hist1[i][j] < hist2[i][i]) {
                        I = I + hist2[i][j];
                    }
                    else {
                        I = I + hist1[i][j];
                    }
                }
            }
        }
        return I;
    }
    
    /**
     * build vertical STI histogram
     * @author Brian Pak(1.0)
     */
    public int[][][] getVerticalSTIHistogram()
    {
        final int x = getRowHeight();
        final int y = getColumnHeight();
        final int z = getFrameCount();
        
        int logbase2N = (int)(Math.log10(getRowHeight()) / Math.log10(2));
        int N = logbase2N + 1;
        
        int[][][] toReturn = new int[z][N][N];
        ImageProcessor currImg = getImage(1);
        int col = currImg.getWidth()/2;
        
        float[][][] rValues = chromacity("r");
        float[][][] gValues = chromacity("g");
        
        for (int k = 1; k < z; k++) {
            int[][] histogram = new int[N][N];
                    
            // for every row
            for (int i = 0; i < x; i++) {
                
                // for every column
                for (int j = 0; j < y; j++) {
                    if (j == col) {
                        float r = rValues[k][i][j];
                        float g = gValues[k][i][j];
                        
                        histogram = fillHistogram(histogram, r, g);
                    }
                }
            }
            toReturn[k] = histogram;
        }
        return toReturn;
    } 
    
    /**
     * convert extract chromacity r or g out of RGB
     * @author Brian Pak(1.0)
     */
    public float[][][] chromacity(String color)
    {
        final int x = getRowHeight();
        final int y = getColumnHeight();
        final int z = getFrameCount();
        
        float[][][] toReturn = new float[z][x][y];

        ImageProcessor currImg;
        
        // for every frame
        for (int k = 1; k < z; k++) {
            currImg = getImage(k);
            
            // for every row
            for (int i = 0; i < x; i++) {
                
                // for every column
                for (int j = 0; j < y; j++){
                    int pixel = currImg.getPixel(x, y);
                    
                    Color c = new Color(pixel);
                    int R = c.getRed();
                    int G = c.getGreen();
                    int B = c.getBlue();
        
                    float r = 0;
                    float g = 0;
                    // ignore B
        
                    // color must not be (0, 0, 0) to avoid divide-by-0
                    if (!isRGBblack(R, G, B)) 
                    {
                        //Added float typecast as Java has weird integer division policies
                        r = R / (float)(R + G + B);
                        g = G / (float)(R + G + B);
                    }
                    
                    if (color.equals("r")) {
                        toReturn[k - 1][i][j] = r;
                    }
                    else if (color.equals("g")) {
                        toReturn[k - 1][i][j] = g;
                    }
                }
            }
        }
        return toReturn;
    }
    
    private boolean isRGBblack(int R, int G, int B)
    {
        if (R == 0 && G == 0 && B == 0)
        {
            return true;
        }
        return false;
    }
    
    public boolean opened()
    {
        return imstack != null;
    }
}
