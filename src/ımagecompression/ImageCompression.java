package ımagecompression;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import static java.lang.Math.cos;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


public class ImageCompression extends javax.swing.JFrame{

    private static BufferedImage originalImage;
    private static File inputFile;
    public static File output;
    public static String outputName;

    public static void postResult (String filename) {
        if (op==1) {
            output = new File(System.getProperty("user.home") + "/Desktop/" + filename + ".jpeg");
        }
        else{
            output = new File(System.getProperty("user.home") + "/Desktop/" + filename + ".jp2");
        }
		
		 try { 
		        ImageIO.write(originalImage, "jpg", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Conversion complete!");
	        
	}
    
    public static void input(){
        JOptionPane.showMessageDialog(null, "Please select an image file you would like to convert.");
		JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("RGB Images", "rgb");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	inputFile = chooser.getSelectedFile();
        	JOptionPane.showMessageDialog(null,("You chose to open this file: " + chooser.getSelectedFile().getName()));
        }
       outputName = JOptionPane.showInputDialog("What will be the name of the new image?");
    }
    //main(String[] args) comp()
   public static void comp() { 
   
       ImageCompression test= new ImageCompression();  
       test.processDCTDWTTransformation(inputFile,1);   
 }
    int totalItemCharged=0;
    // comp_jpg2()   main(String[] args)
   public static void comp_jpg2() { 
    JOptionPane.showMessageDialog(null, "Please select the image file you would like to compare.");
		JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("RGB Images", "rgb");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	inputFile = chooser.getSelectedFile();
        	JOptionPane.showMessageDialog(null,("You chose to open this file: " + chooser.getSelectedFile().getName()));
        }
      
       
       ImageCompression test= new ImageCompression();  
       test.processDCTDWTTransformation(inputFile,-1);   
 }
   
  
   
    private static int WIDTH = 512;
    private static int HEIGHT = 512;
    private static int MAX_PIXEL_VAL = 255;
    private static int BLOCK_SIZE = 8;
    private static float ONE_BY_4 = (float) 1 / 4;
    private static int BASE_COEFFICINT = 4096;
    
    BufferedImage originalImg = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    BufferedImage DCTImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    BufferedImage DWTImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
   
    double[][] redChannelMat_DWT = new double[HEIGHT][WIDTH];
    double[][] greenChannelMat_DWT = new double[HEIGHT][WIDTH];
    double[][] blueChannelMat_DWT = new double[HEIGHT][WIDTH];
    
    
    int[][] redChannelMat = new int[HEIGHT][WIDTH];
    int[][] greenChannelMat = new int[HEIGHT][WIDTH];
    int[][] blueChannelMat = new int[HEIGHT][WIDTH];
    
    int[][] redChannelMat_DCT = new int[HEIGHT][WIDTH];
    int[][] greenChannelMat_DCT = new int[HEIGHT][WIDTH];
    int[][] blueChannelMat_DCT = new int[HEIGHT][WIDTH];
  

    static double[][] cosineBlockMatrix = new double[BLOCK_SIZE][BLOCK_SIZE];
    
    int[][] redChannelMat_IDCT = new int[HEIGHT][WIDTH];
    int[][] greenChannelMat_IDCT = new int[HEIGHT][WIDTH];
    int[][] blueChannelMat_IDCT = new int[HEIGHT][WIDTH];
    
    int[][] redChannelMat_IDWT = new int[HEIGHT][WIDTH];
    int[][] greenChannelMat_IDWT = new int[HEIGHT][WIDTH];
    int[][] blueChannelMat_IDWT = new int[HEIGHT][WIDTH];
    
    JFrame imageFrame = new JFrame();
    GridBagLayout gridBagLayout = new GridBagLayout();
    CardLayout cardLayout = new CardLayout();
    
    JPanel imagePanel = new JPanel();
    JLabel DCTLabel = new JLabel();
    JLabel DWTLable = new JLabel();

    JLabel DCTLabelText = new JLabel();
    JLabel DWTLabelText = new JLabel(); 
   
     int iteration = 1;
     static int  op;
     
    public void processDCTDWTTransformation(File file,int coefficientNum) {
        try {                       
      
            InputStream inputStream = new FileInputStream(file);
         
            long fileLength = file.length();
            byte[] bytes = new byte[(int) fileLength];                                    

            int offset = 0;
            int readCount = 0;
            while (offset < bytes.length&& (readCount = inputStream.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += readCount;
            }

            for (int row = 0; row < BLOCK_SIZE; row++){
                for (int column = 0; column < BLOCK_SIZE; column++){
                    cosineBlockMatrix[row][column] = cos((2 * row + 1) * column * 3.14159 / 16.00); }}
            
             int index = 0;
            for (int row = 0; row < HEIGHT; row++) {
                for (int column = 0; column < WIDTH; column++) {
                    int red = bytes[index];
                    int green = bytes[index + HEIGHT * WIDTH];  
                    int blue = bytes[index + HEIGHT * WIDTH * 2];
 
                    green = green & 0xFF;
                    red = red & 0xFF;
                    blue = blue & 0xFF;

                    redChannelMat[row][column] = red;
                    blueChannelMat[row][column] = blue;
                    greenChannelMat[row][column] = green;

                   
                   int pix = 0xff000000 | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff);
                   originalImg.setRGB(column, row, pix);
                    index++;
                }
            }
            
              if (coefficientNum > 0) {
                  for (int idx = BASE_COEFFICINT; idx <= WIDTH * HEIGHT; idx = idx + BASE_COEFFICINT) {
                coefficientNum = idx;
                int m = coefficientNum / BASE_COEFFICINT;
                performDCT_TransformQuantize(redChannelMat, greenChannelMat, blueChannelMat, m);
                performInverseDCTTransform();
                redChannelMat_DWT = DWTStandardDecomposition(redChannelMat, coefficientNum);
                greenChannelMat_DWT = DWTStandardDecomposition(greenChannelMat, coefficientNum);
                blueChannelMat_DWT = DWTStandardDecomposition(blueChannelMat, coefficientNum);

                redChannelMat_IDWT = IDWTComposition(redChannelMat_DWT);
                greenChannelMat_IDWT = IDWTComposition(greenChannelMat_DWT);
                blueChannelMat_IDWT = IDWTComposition(blueChannelMat_DWT);
                }
              
               if(op == 1){
                   DCTTransform();
                originalImage=DCTImage;}
               else{
                   DWTTransform();
                   originalImage=DWTImage;
               }
                postResult(outputName);
            } else {
                  iteration = 1;
                for (int idx = BASE_COEFFICINT; idx <= WIDTH * HEIGHT; idx = idx + BASE_COEFFICINT) {
            System.out.println("else calisti");
                    coefficientNum = idx;
                    int m = coefficientNum / BASE_COEFFICINT;
                    performDCT_TransformQuantize(redChannelMat, greenChannelMat, blueChannelMat, m);
                    performInverseDCTTransform();

                    redChannelMat_DWT = DWTStandardDecomposition(redChannelMat, coefficientNum);
                    greenChannelMat_DWT = DWTStandardDecomposition(greenChannelMat, coefficientNum);
                    blueChannelMat_DWT = DWTStandardDecomposition(blueChannelMat, coefficientNum);

                    redChannelMat_IDWT = IDWTComposition(redChannelMat_DWT);
                    greenChannelMat_IDWT = IDWTComposition(greenChannelMat_DWT);
                    blueChannelMat_IDWT = IDWTComposition(blueChannelMat_DWT);

                    try {
                         System.out.println("else try calisti");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) { 
                        System.out.println("else catch calisti");
                    }
                } 
             }
           
              }  catch (Exception e) {
            System.out.println("sonuncu catch çalıştı");
       }
     }
    
    
    private void DCTTransform(){
        for (int row = 0; row < HEIGHT; row++) {
            for (int column = 0; column < WIDTH; column++) {

                int pixValue = 0xff000000 | ((redChannelMat_IDCT[row][column] & 0xff) << 16) | ((greenChannelMat_IDCT[row][column] & 0xff) << 8) | (blueChannelMat_IDCT[row][column] & 0xff);
                DCTImage.setRGB(column, row, pixValue);}
      }        
    }
    
    private void DWTTransform(){
        for (int row = 0; row < HEIGHT; row++) {
            for (int column = 0; column < WIDTH; column++) {

                int pixValue = 0xff000000 | (((int) redChannelMat_IDWT[row][column] & 0xff) << 16) | (((int) greenChannelMat_IDWT[row][column] & 0xff) << 8) | ((int) blueChannelMat_IDWT[row][column] & 0xff);
                DWTImage.setRGB(column, row, pixValue);}
      }        
    }
    
      
    
    private static double[][] performMatrixTranspose(double[][] matrix) {
        double[][] tempMat = new double[HEIGHT][WIDTH];
        for (int row = 0; row < HEIGHT; row++)
            for (int column = 0; column < WIDTH; column++)
                tempMat[row][column] = matrix[column][row];

        return tempMat;
    }


    double[][] DWTStandardDecomposition(int[][] matrix, int n) {
        double[][] DWTMatrix = new double[HEIGHT][WIDTH];
        for (int row = 0; row < HEIGHT; row++)
            for (int column = 0; column < WIDTH; column++)
                DWTMatrix[row][column] = matrix[row][column];

        for (int row = 0; row < WIDTH; row++)
            DWTMatrix[row] = getDecompositionArray(DWTMatrix[row]);

        DWTMatrix = performMatrixTranspose(DWTMatrix);
        for (int col = 0; col < HEIGHT; col++)
            DWTMatrix[col] = getDecompositionArray(DWTMatrix[col]);

        DWTMatrix = performMatrixTranspose(DWTMatrix);
        DWTMatrix = doZigZagTraversal(DWTMatrix, n);

        return DWTMatrix;
    }

    private double[] getDecompositionArray(double[] array) {
        int height = array.length;
        while (height > 0) {
            array = decompositionStep(array, height);
            height = height / 2;
        }
        return array;
    }

    /**
     * @return Düşük ve yüksek geçiş ayrıştırılması
     */

    private double[] decompositionStep(double[] array, int height) {
        double[] dArray = Arrays.copyOf(array, array.length);
        for (int index = 0; index < height / 2; index++) {
            dArray[index] = (array[2 * index] + array[2 * index + 1]) / 2;
            dArray[height / 2 + index] = (array[2 * index] - array[2 * index + 1]) / 2;
        }
        return dArray;
    }


    /**
     * DWT işleminden sonra görüntünün yeniden oluşturulması
     *
     */

    static int[][] IDWTComposition(double[][] matrix) {
        int[][] IDWTMatrix = new int[HEIGHT][WIDTH];

        
        matrix = performMatrixTranspose(matrix);
        for (int row = 0; row < WIDTH; row++) {
            matrix[row] = getCompositionArray(matrix[row]);
        }

        matrix = performMatrixTranspose(matrix);
        for (int col = 0; col < HEIGHT; col++) {
            matrix[col] = getCompositionArray(matrix[col]);
        }

        for (int row = 0; row < HEIGHT; row++) {
            for (int column = 0; column < WIDTH; column++) {
                IDWTMatrix[row][column] = (int) Math.round(matrix[row][column]);
                IDWTMatrix[row][column] = IDWTMatrix[row][column] < 0 ? 0 : (IDWTMatrix[row][column] > MAX_PIXEL_VAL ? MAX_PIXEL_VAL : IDWTMatrix[row][column]);
            }
        }

        return IDWTMatrix;
    }

    private static double[] getCompositionArray(double[] array) {
        int height = 1;
        while (height <= array.length) {
            array = compositionStep(array, height);
            height = height * 2;
        }
        return array;
    }

    private static double[] compositionStep(double[] array, int height) {
        double[] dArray = Arrays.copyOf(array, array.length);
        for (int index = 0; index < height / 2; index++) {
            dArray[2 * index] = array[index] + array[height / 2 + index];
            dArray[2 * index + 1] = array[index] - array[height / 2 + index];
        }
        return dArray;
    }
    
    
    void performDCT_TransformQuantize(int[][] redMatrix, int[][] greenMatrix, int[][] blueMatrix, int m) {
        

        for (int row = 0; row < HEIGHT; row += BLOCK_SIZE) {
            for (int column = 0; column < WIDTH; column += BLOCK_SIZE) {

                double[][] redChannelBlock = new double[BLOCK_SIZE][BLOCK_SIZE];
                double[][] greenChannelBlock = new double[BLOCK_SIZE][BLOCK_SIZE];
                double[][] blueChannelBlock = new double[BLOCK_SIZE][BLOCK_SIZE];

                for (int u = 0; u < BLOCK_SIZE; u++) {
                    for (int v = 0; v < BLOCK_SIZE; v++) {

                        float cu = u == 0 ? 0.707f : 1.0f, cv = v == 0 ? 0.707f : 1.0f;
                        float redResult = 0.00f, greenResult = 0.00f, blueResult = 0.00f;

                        for (int xAxis = 0; xAxis < BLOCK_SIZE; xAxis++) {
                            for (int yAxis = 0; yAxis < BLOCK_SIZE; yAxis++) {
                                redResult += (int) redMatrix[row + xAxis][column + yAxis] * cosineBlockMatrix[xAxis][u] * cosineBlockMatrix[yAxis][v];
                                greenResult += (int) greenMatrix[row + xAxis][column + yAxis] * cosineBlockMatrix[xAxis][u] * cosineBlockMatrix[yAxis][v];
                                blueResult += (int) blueMatrix[row + xAxis][column + yAxis] * cosineBlockMatrix[xAxis][u] * cosineBlockMatrix[yAxis][v];
                            }
                        }

                        redChannelBlock[u][v] = (int) Math.round(redResult * ONE_BY_4 * cu * cv);
                        greenChannelBlock[u][v] = (int) Math.round(greenResult * ONE_BY_4 * cu * cv);
                        blueChannelBlock[u][v] = (int) Math.round(blueResult * ONE_BY_4 * cu * cv);
                    }
                }

                redChannelBlock = doZigZagTraversal(redChannelBlock, m);
                greenChannelBlock = doZigZagTraversal(greenChannelBlock, m);
                blueChannelBlock = doZigZagTraversal(blueChannelBlock, m);

                for (int uBlockIndex = 0; uBlockIndex < BLOCK_SIZE; uBlockIndex++) {
                    for (int vBlockIndex = 0; vBlockIndex < BLOCK_SIZE; vBlockIndex++) {
                        redChannelMat_DCT[row + uBlockIndex][column + vBlockIndex] = (int) redChannelBlock[uBlockIndex][vBlockIndex];
                        greenChannelMat_DCT[row + uBlockIndex][column + vBlockIndex] = (int) greenChannelBlock[uBlockIndex][vBlockIndex];
                        blueChannelMat_DCT[row + uBlockIndex][column + vBlockIndex] = (int) blueChannelBlock[uBlockIndex][vBlockIndex];
                    }
                }
            }
        }
    }
    
  

    public double[][] doZigZagTraversal(double[][] matrix, int m) {
        int row = 0;
        int column = 0;
        int length = matrix.length - 1;
        int count = 1;
       
        matrix[row][column] = count > m ? 0 : matrix[row][column];
        count++;

        while (true) {

            column++;
            matrix[row][column] = count > m ? 0 : matrix[row][column];
            count++;

            while (column != 0) {
                row++;
                column--;
                matrix[row][column] = count > m ? 0 : matrix[row][column];
                count++;
            }
            row++;
            if (row > length) {
                row--;
                break;
            }

            matrix[row][column] = count > m ? 0 : matrix[row][column];
            count++;

            while (row != 0) {
                row--;
                column++;
                matrix[row][column] = count > m ? 0 : matrix[row][column];
                count++;
            }
        }

        while (true) {
            column++;
            count++;

            if (count > m) {
                matrix[row][column] = 0;
            }

            while (column != length) {
                column++;
                row--;
                matrix[row][column] = count > m ? 0 : matrix[row][column];
                count++;
            }

            row++;
            if (row > length) {
                row--;
                break;
            }
            matrix[row][column] = count > m ? 0 : matrix[row][column];
            count++;

            while (row < length) {
                row++;
                column--;
                matrix[row][column] = count > m ? 0 : matrix[row][column];
                count++;
            }
        }
        return matrix;
    }    
    

  /*
   * IDCT hesaplaması
   */

    public void performInverseDCTTransform() {
        
        for (int row = 0; row < HEIGHT; row += BLOCK_SIZE) {
            for (int column = 0; column < WIDTH; column += BLOCK_SIZE) {

                for (int x = 0; x < BLOCK_SIZE; x++) {
                    for (int y = 0; y < BLOCK_SIZE; y++) {

                          float redIDCTVal = 0.00f, greenIDCTVal = 0.00f, blueIDCTVal = 0.00f;

                        for (int u = 0; u < BLOCK_SIZE; u++) {
                            for (int v = 0; v < BLOCK_SIZE; v++) {
                                float fCu = u == 0 ? 0.707f : 1.0f, fCv = v == 0 ? 0.707f : 1.0f;

                              redIDCTVal += fCu * fCv * redChannelMat_DCT[row + u][column + v] * cosineBlockMatrix[x][u] * cosineBlockMatrix[y][v];
                               greenIDCTVal += fCu * fCv * greenChannelMat_DCT[row + u][column + v] * cosineBlockMatrix[x][u] * cosineBlockMatrix[y][v];
                                blueIDCTVal += fCu * fCv * blueChannelMat_DCT[row + u][column + v] * cosineBlockMatrix[x][u] * cosineBlockMatrix[y][v];
                            }
                        }

                        redIDCTVal = redIDCTVal * ONE_BY_4;
                        greenIDCTVal = greenIDCTVal * ONE_BY_4;
                        blueIDCTVal = blueIDCTVal * ONE_BY_4;

                        redIDCTVal = redIDCTVal <= 0 ? 0 : (redIDCTVal >= MAX_PIXEL_VAL ? MAX_PIXEL_VAL : redIDCTVal);
                        greenIDCTVal = greenIDCTVal <= 0 ? 0 : (greenIDCTVal >= MAX_PIXEL_VAL ? MAX_PIXEL_VAL : greenIDCTVal);
                        blueIDCTVal = blueIDCTVal <= 0 ? 0 : (blueIDCTVal >= MAX_PIXEL_VAL ? MAX_PIXEL_VAL : blueIDCTVal);

                        redChannelMat_IDCT[row + x][column + y] = (int) redIDCTVal;
                        greenChannelMat_IDCT[row + x][column + y] = (int) greenIDCTVal;
                        blueChannelMat_IDCT[row + x][column + y] = (int) blueIDCTVal;
                    }
                }
            }
        }

    }

}
