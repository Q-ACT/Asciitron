package com.qact;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static String directory = "";
    static File inputFile;
    static BufferedImage image;
    static int imgWidth;
    static int imgHeight;
    static int res;
    static String luminanceScale  = "";
    static final String lum1 = "@MBHENR#KWXDFdet[})=~!-/_';,:`. ";
    static final String lum2 = "@%#WXFdt*+=~!-;,:`. ";
    static final String lum3 = "@%#*+=-:. ";
    static final String lum4 = "@0=: ";
    public static void main(String[] args) throws IOException {
        boolean inApp = true;
        while(inApp) {
            //Get the directory for image
            do {
                image = null;
                System.out.println("Enter Image Directory");
                directory = scanner.nextLine();
                inputFile = new File(directory);
                try {
                    image = ImageIO.read(inputFile);
                } catch (Exception e) {
                    if (!inputFile.exists()) {
                        System.out.println("Error: could not find a file at " + directory);
                    }
                    if (image == null && inputFile.exists()) {
                        System.out.println("Error: " + inputFile.getName() + " is not a valid image file");
                    }
                }

            } while (!inputFile.isFile() || image == null);

            //Get desired contrast level
            do {
                System.out.println("Enter Contrast level [1][2][3][4]");
                switch (scanner.nextLine()) {
                    case "1" -> {
                        System.out.println("Set Contrast to Minimum(1)");
                        luminanceScale = lum1;
                    }
                    case "2" -> {
                        System.out.println("Set Contrast to 2");
                        luminanceScale = lum2;
                    }
                    case "3" -> {
                        System.out.println("Set Contrast to 3");
                        luminanceScale = lum3;
                    }
                    case "4" -> {
                        System.out.println("Set Contrast to Maximum(4)");
                        luminanceScale = lum4;
                    }
                    default -> {
                        System.out.println("Invalid Entry: please enter a number 1-4");
                        luminanceScale = "";
                    }
                }
            } while (luminanceScale.equals(""));
            do {
                System.out.println("Enter desired image width");
                try {
                    res = 0;
                    res = scanner.nextInt();
                }catch (InputMismatchException e){
                    System.out.println("Invalid Entry: please enter an integer");
                }
            } while (res == 0);
            image = getScaledBWImage(image, res);
            imgHeight = image.getHeight();
            imgWidth = image.getWidth();
            System.out.println("Set image resolution to " + imgWidth +"x"+ imgHeight);
            System.out.println("Press enter to generate image");
            System.out.println("Processing...");
            System.out.println(pixelToAscii(image.getRaster().getPixels(0,0,imgWidth,imgHeight,(float[])null)));
            System.out.println("Create Another? [y/n]");
            scanner.nextLine();
            if (!scanner.nextLine().equalsIgnoreCase("y")){
                inApp = false;
                System.out.println("Quitting...");
            }
        }
    }

    public static String pixelToAscii(float[] pixels){
        String asciiImage = "";
        for(int i = 0; i < pixels.length; i++){
            asciiImage = asciiImage.concat(" " + luminanceScale.charAt((int) (pixels[i] / 255f * (luminanceScale.length()-1))) + " ");
            if(i%imgWidth+1 == imgWidth){
                asciiImage = asciiImage.concat("\n");
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
