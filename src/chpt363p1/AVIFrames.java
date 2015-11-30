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
     * @param histogram the current histogram of size [N + 1][N + 1] where n
     *                  equals log_2 (image height)
     * @param r the normalized float value of r for that pixel
     * @param g the normalized float value of g for that pixel
     * @return histogram of every frame
     * @author Brian Pak(1.0)
     */
    private int[][] fillHistogram(int[][] histogram, float r, float g)
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
    
    /**
     * INCOMPLETE!!!!!!!!
     * @return an image with size [column total][frame]
     */
    public int[][] getVerticalHistDifferences()
    {
        //Get dimensions
        final int colTotal = getRowHeight(); //rowHeight = number of columns
        final float[][][] tmp = normalize(getVerticalSTIHistograms(0));
        final int frame = tmp.length;
        final int r = tmp[0].length;
        final int g = tmp[0][0].length;
        
        //Array of column images
        float[][][][] hists = new float[colTotal][frame][r][g];
        hists[0] = tmp;
        for(int i = 1; i < colTotal; i++){//For each column, get histogram
            hists[i] = normalize(getVerticalSTIHistograms(i));
        }
        int[][] toReturn = new int[colTotal][frame]; 
                //Get the I value and place in toReturn 
        return null;      
    }
    
    
    /**
     * 
     * @param hist1 the normalized histograms for a given row or column
     * @param hist2 the normalized histograms for the frame after hist1
     * @return I, the min addition betwen hist1 and hist2, (range 0 to 1)
     *         I ~=1 if similar, ~0 if not
     */
    private float getHistogramIntersection(int[][] hist1, int[][] hist2) 
    {
        final int x = hist1.length;
        final int y = hist1[0].length;
        
        float I = 0;
        
        for (int i = 0; i < x; i++) {// for every row
            for (int j = 0; j < y; j++) {// for every column
                I += Math.min(hist1[i][j], hist2[i][j]);
            }
        }
            
        return I;
    }
    
    /**
     * 
     * @param intarray the array to be normalized
     * @return the normalized array
     * @author Chazz Young(1.0)
     */
    private float[][][] normalize(int[][][] intarray)
    {
        //Get array dimensions
        final int x = intarray.length;
        final int y = intarray[0].length;
        final int z = intarray[0][0].length;
        
        float[][][] toReturn = new float[x][y][z];
        
        //Get the count of the histogram
        int total = 0;
        for(int i = 0; i < x; i++){
            for(int j = 0; j < y; j++){
                for(int k = 0; k < z; k++){
                    total += intarray[i][j][k];
                }
            }
        }
        
        //Normalize the histogram
        for(int i = 0; i < x; i++){
            for(int j = 0; j < y; j++){
                for(int k = 0; k < z; k++){
                    toReturn[i][j][k] = (float)intarray[i][j][k] / (float)total;
                }
            }
        }
        return toReturn;
    }
    
    /**
     * Now accepts column, (Chazz Young)
     * build vertical STI histogram
     * @param col The column to calculate the histograms 
     * @return the histograms for each frame with the given column
     * @author Brian Pak, Chazz Young(1.0)
     */
    public int[][][] getVerticalSTIHistograms(int col)
    {
        final int x = getRowHeight();
        final int y = getColumnHeight();
        final int z = getFrameCount();
        
        int logbase2N = (int)(Math.log10(getRowHeight()) / Math.log10(2));
        int N = logbase2N + 1;
        
        int[][][] toReturn = new int[z][N][N];
        
        //Chromacity values
        float[][][] rValues = chromacity("r");
        float[][][] gValues = chromacity("g");
        
        for (int k = 1; k <= z; k++) {//For each frame
            int[][] histogram = new int[N][N];
            
            //Fill the histogram
            /*for (int i = 0; i < x; i++) {// for every row*/ /**Replaced by col*/
                for (int j = 0; j < y; j++) {// for every column
                    //Get r and g
                    float r = rValues[k][col][j];
                    float g = gValues[k][col][j];
                    
                    //Add to histogram
                    histogram = fillHistogram(histogram, r, g);   
                }
            //}
            toReturn[k] = histogram;
        }
        return toReturn;
    } 
    
    /**
     * convert chromaticity r or g out of RGB
     * @param color either "r" or "g"
     * @author Brian Pak(1.0)
     */
    private float[][][] chromacity(String color)
    {
        final int x = getRowHeight();
        final int y = getColumnHeight();
        final int z = getFrameCount();
        
        float[][][] toReturn = new float[z][x][y];

        ImageProcessor currImg;
        
        // for every frame
        for (int k = 1; k <= z; k++) {
            currImg = getImage(k);
            
            for (int i = 0; i < x; i++) {// for every row
                for (int j = 0; j < y; j++){// for every column
                    int pixel = currImg.getPixel(x, y);
                    
                    Color c = new Color(pixel);
                    int R = c.getRed();
                    int G = c.getGreen();
                    int B = c.getBlue();
        
                    float r = 0;
                    float g = 0;
                    // ignore B
        
                    // color must not be (0, 0, 0) to avoid divide-by-0
                    if (!isRGBblack(R, G, B)){
                        //Added float typecast as Java has weird integer division policies
                        r = (float)R / (float)(R + G + B);
                        g = (float)G / (float)(R + G + B);
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
        //Replaced by Netbeans
        return (R == 0 && G == 0 && B == 0);
    }
    
    public boolean opened()
    {
        return imstack != null;
    }
}
