package jssim;


import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException, org.apache.poi.openxml4j.exceptions.InvalidFormatException, SsimException {
        new Application().addImagesToWordDocument();
    }


    public void addImagesToWordDocument()
            throws IOException, org.apache.poi.openxml4j.exceptions.InvalidFormatException, SsimException {

        FileOutputStream out = new FileOutputStream("word_images.docx");
        File dir1 = new File("D:\\ComparisonImages\\image");
        File dir2 = new File("D:\\ComparisonImages\\image1");
        File[] f = dir1.listFiles();
        File[] g = dir2.listFiles();
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        for (File file : f) {
            for (File file1 : g) {
//                if (file.getName().equals(file1.getName())) {
                SsimCalculator ssimCalculator = new SsimCalculator(file);
                BufferedImage bimg1 = ImageIO.read(new File(file.getAbsolutePath()));
                BufferedImage bimg2 = ImageIO.read(new File(file1.getAbsolutePath()));

                int w1 = bimg1.getWidth();
                int w2 = bimg2.getWidth();
                int h1 = bimg1.getHeight();
                int h2 = bimg2.getHeight();
                if ((w1 != w2) || (h1 != h2)) {
//                    throw new SsimException("Both images should hae the same dimensions");
                } else {
                    long diff = 0;
                    for (int j = 0; j < h1; j++) {
                        for (int i = 0; i < w1; i++) {
                            //Getting the RGB values of a pixel
                            int pixel1 = bimg1.getRGB(i, j);
                            Color color1 = new Color(pixel1, true);
                            int r1 = color1.getRed();
                            int g1 = color1.getGreen();
                            int b1 = color1.getBlue();
                            int pixel2 = bimg2.getRGB(i, j);
                            Color color2 = new Color(pixel2, true);
                            int r2 = color2.getRed();
                            int g2 = color2.getGreen();
                            int b2 = color2.getBlue();
                            //sum of differences of RGB values of the two images
                            long data = Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
                            diff = diff + data;
                        }
                    }

                    double avg = diff / (w1 * h1 * 3);
                    double difference = (avg / 255) * 100;
                    String imgFile1 = file.getName();
                    String imgFile2 = file1.getName();
                    int imgFormat1 = getImageFormat(imgFile1);
                    int imgFormat2 = getImageFormat(imgFile2);
//            if (difference<=20) {
                    r.setText("The difference between " + file.getName() + " and " + file1.getName() + " is : " + String.valueOf(difference) + "%" + "    SSIM -> " + ssimCalculator.compareTo(file1));
                    r.addBreak();
                    String p1 = file.getName();
                    String p2 = file1.getName();
                    r.setText(p1);
                    r.addPicture(new FileInputStream(file), imgFormat1, imgFile1, Units.toEMU(500), Units.toEMU(300));
                    r.setText(p2);
                    r.addPicture(new FileInputStream(file1), imgFormat2, imgFile2, Units.toEMU(500), Units.toEMU(300));
                    r.addBreak();
                    r.addBreak();
//            }
                    System.out.println(diff);
                }
//                }
            }


        }
        doc.write(out);
        out.close();
    }

    private static int getImageFormat(String imgFileName) {
        int format;
        if (imgFileName.endsWith(".emf"))
            format = XWPFDocument.PICTURE_TYPE_EMF;
        else if (imgFileName.endsWith(".wmf"))
            format = XWPFDocument.PICTURE_TYPE_WMF;
        else if (imgFileName.endsWith(".pict"))
            format = XWPFDocument.PICTURE_TYPE_PICT;
        else if (imgFileName.endsWith(".jpeg") || imgFileName.endsWith(".jpg"))
            format = XWPFDocument.PICTURE_TYPE_JPEG;
        else if (imgFileName.endsWith(".png"))
            format = XWPFDocument.PICTURE_TYPE_PNG;
        else if (imgFileName.endsWith(".dib"))
            format = XWPFDocument.PICTURE_TYPE_DIB;
        else if (imgFileName.endsWith(".gif"))
            format = XWPFDocument.PICTURE_TYPE_GIF;
        else if (imgFileName.endsWith(".tiff"))
            format = XWPFDocument.PICTURE_TYPE_TIFF;
        else if (imgFileName.endsWith(".eps"))
            format = XWPFDocument.PICTURE_TYPE_EPS;
        else if (imgFileName.endsWith(".bmp"))
            format = XWPFDocument.PICTURE_TYPE_BMP;
        else if (imgFileName.endsWith(".wpg"))
            format = XWPFDocument.PICTURE_TYPE_WPG;
        else {
            return 0;
        }
        return format;
    }
}
