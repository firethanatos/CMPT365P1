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
    
    /**
     * @author Chazz Young(0.0)
     */
    public int getFrameCount()
    {
        return imstack.getSize();
    }
    
    /**
     * Returns a double array of int values to be used for tone volumes
     * @param frame the frame number
     * @return the volumes for each pixel
     * @author Chazz Young(0.0)
     */
    public int[][] getPixelVolumes(int frame)
    {
        ImageProcessor img = getImage(frame);
        int[][] pixels = img.getIntArray();
        
        //Round every pixel value  to a greyscale 8 but luminence
        for(int i = 0; i < 64; i++){
            for(int j = 0; j < 64; j++){
                //Convert (grey) RGB to luminence
                pixels[i][j] = convertToLuminence(pixels[i][j]);
            }
        }
        return pixels;
    }
    
    /**
     * This function converts an integer pixel value to a 0 to 100 luminence measure
     * @param pixel the pixel value in sRGB format
     * @return a luminence value between 0 and 100 scaled by 100/16
     * @author Chazz Young(0./0)
     */
    private int convertToLuminence(int pixel)
    {
        Color c = new Color(pixel);
        double ans = (double)c.getRed() * 0.299; 
        ans += (double)c.getGreen() * 0.587; 
        ans += (double)c.getBlue() * 0.114;
        ans *= 100.0/255.0; //ans is now 0 to 100
        ans *= 16.0/100.0;//ans is now 0 to 16
        pixel = (new Double(ans)).intValue();
        pixel = (new Double((double)pixel * 100.0/16.0)).intValue();
        return pixel;
    }
    
    /**
     * @author Chazz Young(0.0)
     */
    public boolean opened()
    {
        if(imstack != null){
            return true;
        }else{
            return false;
        }
    }
}
