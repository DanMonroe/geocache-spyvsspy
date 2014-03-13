import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class spy {
	private static String whiteImage = "white.png";
	private static String blackImage = "black.png";
	private static String coordsImage = "coords_pathtonowhere.png";
	private static String outWhiteImage = "outWhite.png";
	private static String outBlackImage = "outBlack.png";
	private static String outCoordsImage = "outCoords.png";
	
	private static void go() {
		BufferedImage bufImageWhite = null;
		BufferedImage bufImageBlack = null;
		BufferedImage bufImageCoords = null;
		BufferedImage bufImageOutWhite = null;
		BufferedImage bufImageOutBlack = null;
		BufferedImage bufImageOutCoords = null;

		Random randomGenerator = new Random();

		boolean hideSpies = false;	// false for production
		
		try {
			bufImageWhite = loadImage( whiteImage );
			bufImageBlack = loadImage( blackImage );
			bufImageCoords = loadImage( coordsImage );
			
		    byte whiteBytes[] = accessBytes(bufImageWhite);
		    System.out.println("Byte length of white image: " + whiteBytes.length);
		    byte blackBytes[] = accessBytes(bufImageBlack);
		    System.out.println("Byte length of black image: " + blackBytes.length);
		    byte coordsBytes[] = accessBytes(bufImageCoords);
		    System.out.println("Byte length of coords image: " + coordsBytes.length);
		    
		    
		    int imgCols = bufImageWhite.getWidth(null); 
		    int imgRows = bufImageWhite.getHeight(null); 


		    int pixel = 0;
		    
		    int[][][] originalCoordsPixel = new int[imgCols][imgRows][4];
		    for( int col = 0; col < imgCols; col++ ) {
		    	for(int row = 0; row < imgRows; row++ ){
		    		pixel = bufImageCoords.getRGB(col,row);
		    		originalCoordsPixel[col][row][0] = pixel & 255; 	//blue
		    		originalCoordsPixel[col][row][1] = (pixel>>8) & 255; // green
		    		originalCoordsPixel[col][row][2] = (pixel>>16) & 255; // red
		    		originalCoordsPixel[col][row][3] = (pixel>>24) & 255; // alpha
		    	}
		    }

		    // Build output files
		    bufImageOutWhite = new BufferedImage(imgCols, imgRows, BufferedImage.TYPE_INT_ARGB);
		    bufImageOutBlack = new BufferedImage(imgCols, imgRows, BufferedImage.TYPE_INT_ARGB);
		    bufImageOutCoords = new BufferedImage(imgCols, imgRows, BufferedImage.TYPE_INT_ARGB);
				int partialAlpha = 0;
				int fullAlpha = 0;		    
		    for( int col = 0; col < imgCols; col++ ) {
		    	for(int row = 0; row < imgRows; row++ ){
			    
		    		pixel = bufImageWhite.getRGB(col,row);
				    int white_blue = pixel & 255; 
				    int white_green = (pixel>>8) & 255; 
				    int white_red = (pixel>>16) & 255;
				    int white_alpha = (pixel>>24) & 255; 

		    		pixel = bufImageBlack.getRGB(col,row);
				    int black_blue = pixel & 255; 
				    int black_green = (pixel>>8) & 255; 
				    int black_red = (pixel>>16) & 255;
				    int black_alpha = (pixel>>24) & 255; 

		    		pixel = bufImageCoords.getRGB(col,row);
				    int coords_blue = pixel & 255; 
				    int coords_green = (pixel>>8) & 255; 
				    int coords_red = (pixel>>16) & 255;
				    int coords_alpha = (pixel>>24) & 255; 

				    
				    if ((coords_red == 0) || (coords_green == 0) || (coords_blue == 0)) {
				    	partialAlpha++;
				    		white_alpha = 254;

				    		black_alpha = 254;
				    } else {
				    	fullAlpha++;
				    	
				    	// Here are the remaining pixels.
				    	// Need to randomly pick one of three cases:
				    	// 1: both alphas are 255
				    	// 2: white alpha is 254, black is 255
				    	// 3: white alpha is 255, black is 254
				    	int randomInt = randomGenerator.nextInt(3);
				    	
				    	switch(randomInt) {
				    	case 0:
				    		white_alpha = 255;
				    		black_alpha = 255;
				    		//remove

				    		if(hideSpies){
				    			white_red = 255;
				    			white_green = 255;
				    			white_blue = 255;
				    			black_red = 255;
				    			black_green = 255;
				    			black_blue = 255;
				    		}

				    		break;
				    	case 1:
				    		white_alpha = 254;
				    		black_alpha = 255;
				    		// remove
				    		if(hideSpies){
				    			white_red = 0;
				    			white_green = 0;
				    			white_blue = 0;
				    			black_red = 255;
				    			black_green = 255;
				    			black_blue = 255;
				    		}
				    		break;
				    	case 2:
				    		white_alpha = 255;
				    		black_alpha = 254;
				    		// remove
				    		if(hideSpies){

				    			white_red = 255;
				    			white_green = 255;
				    			white_blue = 255;
				    			black_red = 0;
				    			black_green = 0;
				    			black_blue = 0;
				    		}
				    		break;
				    	default:
				    		System.out.println("Shouldn't be here");
				    		break;
				    	}
				    }
				    
				    
				    // new white
				    int whiteRGB = (white_alpha<<24)|(white_red<<16)|(white_green<<8)|white_blue;
				    bufImageOutWhite.setRGB(col, row, whiteRGB);

				    // new black
				    int blackRGB = (black_alpha<<24)|(black_red<<16)|(black_green<<8)|black_blue;
				    bufImageOutBlack.setRGB(col, row, blackRGB);

				    // new coords
				    if(((255-white_alpha)==1) && ((255-black_alpha)==1)) {
				    	// set on
				    	coords_alpha = 255;
				    	coords_red = 0;
				    	coords_green = 0;
				    	coords_blue = 0;
				    } else {
				    	// background
				    	coords_alpha = 255;

				    	coords_red = 255;
				    	coords_green = 255;
				    	coords_blue = 255;
				    }
				    
				    
				    int coordsRGB = (coords_alpha<<24)|(coords_red<<16)|(coords_green<<8)|coords_blue;
				    bufImageOutCoords.setRGB(col, row, coordsRGB);
				    
		    	}
		    }
		    System.out.println("partialAlpha " + partialAlpha);
		    System.out.println("fullAlpha " + fullAlpha);

	    	File outputfile_white = new File(outWhiteImage);
	    	boolean bSuccess_white = ImageIO.write( bufImageOutWhite, "png", outputfile_white);

	    	File outputfile_black = new File(outBlackImage);
	    	boolean bSuccess_black = ImageIO.write( bufImageOutBlack, "png", outputfile_black);

	    	File outputfile_coords = new File(outCoordsImage);
	    	boolean bSuccess_coords = ImageIO.write( bufImageOutCoords, "png", outputfile_coords);

		} catch (Exception e) {
			System.out.println("Exception e: " + e.toString() );
		}
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel labelWhite = new JLabel(new ImageIcon(bufImageWhite));
		JLabel labelWhiteOut = new JLabel(new ImageIcon(bufImageOutWhite));
		JLabel labelBlack = new JLabel(new ImageIcon(bufImageBlack));
		JLabel labelBlackOut = new JLabel(new ImageIcon(bufImageOutBlack));
		JLabel labelCoords= new JLabel(new ImageIcon(bufImageCoords));
		JLabel labelCoordsOut= new JLabel(new ImageIcon(bufImageOutCoords));

		JPanel panel = new JPanel();
		panel.add(labelWhite);
		panel.add(labelWhiteOut);
		panel.add(labelBlack);
		panel.add(labelBlackOut);
		panel.add(labelCoords);
		panel.add(labelCoordsOut);
		
		JScrollPane scrollPane = new JScrollPane(panel);
		
		scrollPane.setPreferredSize(new Dimension(1280,370));
				
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

	}

	private static BufferedImage loadImage(String fileName) {
		BufferedImage img = null;
		try {
			File tempFile = new File(fileName);
			img = ImageIO.read( tempFile );
			System.out.println("Read " + fileName);
		} catch (IOException e) { 
			System.out.println("Could not read image: " + fileName); 
		}

		return img;
	}
	
	private static byte[] accessBytes(BufferedImage image){
		WritableRaster raster = image.getRaster();
		DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
		return buffer.getData();
	}

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                go();
            }
        });
    }
}
