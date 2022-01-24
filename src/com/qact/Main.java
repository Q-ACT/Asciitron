package com.qact;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static String directory = "";
    static File inputFile;
    static BufferedImage image;
    static int imgWidth;
    static int imgHeight;
    static final String luminance = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ";
    public static void main(String[] args) throws IOException {
            do {
                System.out.println("Enter Image Directory");
                directory = scanner.nextLine();
                inputFile = new File(directory);
                try {
                    image = ImageIO.read(inputFile);
                }catch (Exception e){
                    if(!inputFile.exists()){
                        System.out.println("Error: could not find a file at " + directory);
                    }
                    if(image == null && inputFile.exists()){
                        System.out.println("Error: " + inputFile.getName() + " is not a valid image file");
                    }
                }

            } while (!inputFile.isFile()||image == null);
            image = getScaledBWImage(image,128);
            imgHeight = image.getHeight();
            imgWidth = image.getWidth();
        System.out.println("Processing...");
        System.out.println("Width: " + imgWidth);
        System.out.println("Height: " + imgHeight);
        System.out.println(Arrays.toString(image.getRaster().getPixels(0, 0, imgWidth, imgHeight, (int[]) null)));
        System.out.println(pixelToAscii(image.getRaster().getPixels(0,0,imgWidth,imgHeight,(int[])null)));
    }

    public static String pixelToAscii(int[] pixels){
        String asciiImage = "";
        for(int i = 0; i < imgHeight-1; i++){
            for(int j = 0; j < imgWidth-1; i++) {
                int currPix = i * (imgWidth);
                asciiImage = asciiImage.concat(String.valueOf(luminance.charAt(pixels[currPix] / 255 * 70)));
            }
        }
        return asciiImage;
    }

    public static BufferedImage getScaledBWImage(BufferedImage image, int width){
        BufferedImage scaledImage = new BufferedImage(width, width*image.getHeight()/image.getWidth(), 10);
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, width, width*image.getHeight()/image.getWidth(), 0, 0, image.getWidth(), image.getHeight(), null);
        graphics2D.dispose();
        return scaledImage;
    }
}
