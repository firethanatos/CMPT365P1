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
        img = img.resize(64, 64);
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
        final int y = getColumnHeight(); //y is heigh tof the column
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
        final int y = getRowHeight(); //y is heigh tof the column
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
    
    public boolean opened()
    {
        return imstack != null;
    }
}
