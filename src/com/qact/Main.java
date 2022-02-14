package com.qact;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static String directory = "";
    static File inputFile;
    static BufferedImage image;
    static int imgWidth;
    static int imgHeight;
    static int res;
    static String luminanceScale  = "";
    static float contrast;
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
                        System.out.println("Invalid Entry: No file at " + directory);
                    }
                    if (image == null && inputFile.exists()) {
                        System.out.println("Invalid Entry: " + inputFile.getName() + " is not a valid image file");
                    }
                }

            } while (!inputFile.isFile() || image == null);

            //Get contrast level
            boolean invalidContrast;
            do {
                invalidContrast = false;
                System.out.println("Enter contrast level [-255 - 255]");
                try {
                    contrast = Float.parseFloat(scanner.nextLine());
                }catch(NumberFormatException e){
                    System.out.println("Invalid Entry: Input was not a number");
                    invalidContrast = true;
                }
            } while(invalidContrast);

            //Get desired spectrum size
            do {
                System.out.println("Enter spectrum size [1][2][3][4]");
                switch (scanner.nextLine()) {
                    case "1" -> {
                        System.out.println("Set spectrum size to Minimum(1)");
                        luminanceScale = lum1;
                    }
                    case "2" -> {
                        System.out.println("Set spectrum size to 2");
                        luminanceScale = lum2;
                    }
                    case "3" -> {
                        System.out.println("Set spectrum size to 3");
                        luminanceScale = lum3;
                    }
                    case "4" -> {
                        System.out.println("Set spectrum size to Maximum(4)");
                        luminanceScale = lum4;
                    }
                    default -> {
                        System.out.println("Invalid Entry: Please enter a number 1-4");
                        luminanceScale = "";
                    }
                }
            } while (luminanceScale.equals(""));

            //get image width
            do {
                System.out.println("Enter desired image width");
                try {
                    res = 0;
                    res = scanner.nextInt();
                }catch (InputMismatchException e){
                    System.out.println("Invalid Entry: Input was not a number");
                }
            } while (res == 0);
            image = getScaledBWImage(image, res);
            imgHeight = image.getHeight();
            imgWidth = image.getWidth();
            image = getContrastImage(imgWidth, imgHeight,image.getRaster().getPixels(0,0,imgWidth,imgHeight,(float[])null),contrast);
            System.out.println("Set image resolution to " + imgWidth +"x"+ imgHeight);
            System.out.println("Processing...");
            File output = new File(inputFile.getPath().replace(inputFile.getName(),"") + "output.txt");
            for (int i = 1; output.exists(); i++){
                output = new File(String.format(inputFile.getPath().replace(inputFile.getName(),"") + "output %d.txt",i));
            }
            Files.write(output.toPath(),pixelToAscii(image.getRaster().getPixels(0,0,imgWidth,imgHeight,(float[])null)));
//            ImageIO.write(getContrastImage(imgWidth, imgHeight,image.getRaster().getPixels(0,0,imgWidth,imgHeight,(float[])null),contrast),"jpg",output);
            System.out.println("Create Another? [y/n]");
            scanner.nextLine();
            if (!scanner.nextLine().equalsIgnoreCase("y")){
                inApp = false;
                System.out.println("Quitting...");
            }
        }
    }

    public static List<String> pixelToAscii(float[] pixels){
        List<String> asciiImage = new ArrayList<>();
        String asciiRow = "";
        for(int i = 0; i < pixels.length; i++){
            asciiRow = asciiRow.concat(luminanceScale.charAt((int) (pixels[i] / 255f * (luminanceScale.length()-1))) + " ");
            if(i%imgWidth+1 == imgWidth){
                asciiImage.add(asciiRow);
                asciiRow = "";
            }
        }
        return asciiImage;
    }

    public static BufferedImage getContrastImage(int width, int height, float[] data,float contrast){
        float[] cData = new float[data.length];
        float factor = (259 * (contrast + 255)) / (255 * (259 - contrast));
        BufferedImage tempImg = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
        for(int i = 0; i < data.length; i++){
            cData[i] = Truncate(factor*(data[i]-128)+128);
        }
        tempImg.getRaster().setPixels(
                tempImg.getRaster().getMinX(),
                tempImg.getRaster().getMinY(),
                tempImg.getRaster().getWidth(),
                tempImg.getRaster().getHeight(),
                cData);
        return tempImg;
    }

    public static float Truncate(float v){
        float value = v;
        if(value < 0){
            value = 0;
        }else if(value > 255){
            value = 255;
        }
        return value;
    }

    public static BufferedImage getScaledBWImage(BufferedImage image, int width){
        BufferedImage scaledImage = new BufferedImage(width, width*image.getHeight()/image.getWidth(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, width, width*image.getHeight()/image.getWidth(), 0, 0, image.getWidth(), image.getHeight(), null);
        graphics2D.dispose();
        return scaledImage;
    }
}
