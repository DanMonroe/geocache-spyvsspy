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

public class SpyVsSpySolve {
	private static String whiteImage = "white_spy.png";
	private static String blackImage = "black_spy.png";
	private static String outCoordsImage = "outCoords.png";

	private static void solve() {
		BufferedImage bufImageWhite = null;
		BufferedImage bufImageBlack = null;
		BufferedImage bufImageCoords = null;

		try {
			bufImageWhite = loadImage(whiteImage);
			bufImageBlack = loadImage(blackImage);
			
		    int imgCols = bufImageWhite.getWidth(null); 
		    int imgRows = bufImageWhite.getHeight(null); 

		    // Build coords file
		    bufImageCoords = new BufferedImage(imgCols, imgRows, BufferedImage.TYPE_INT_ARGB);
		    int coords_blue; 
		    int coords_green; 
		    int coords_red;
		    int coords_alpha = 255; 
		    
		    int pixel = 0;

		    int[][][] whitePixel = new int[imgCols][imgRows][4];
		    int[][][] blackPixel = new int[imgCols][imgRows][4];

		    for( int col = 0; col < imgCols; col++ ) {
		    	for(int row = 0; row < imgRows; row++ ){
		    		pixel = bufImageWhite.getRGB(col,row);
		    		whitePixel[col][row][0] = pixel & 255; 	//blue
		    		whitePixel[col][row][1] = (pixel>>8) & 255; // green
		    		whitePixel[col][row][2] = (pixel>>16) & 255; // red
		    		whitePixel[col][row][3] = (pixel>>24) & 255; // alpha
		    		
		    		pixel = bufImageBlack.getRGB(col,row);
		    		blackPixel[col][row][0] = pixel & 255; 	//blue
		    		blackPixel[col][row][1] = (pixel>>8) & 255; // green
		    		blackPixel[col][row][2] = (pixel>>16) & 255; // red
		    		blackPixel[col][row][3] = (pixel>>24) & 255; // alpha
		    		
		    		if((whitePixel[col][row][3]==254) && (blackPixel[col][row][3]==254)) {
		    		    coords_blue = 0; 
		    		    coords_green = 0; 
		    		    coords_red = 0;
		    		} else {
		    		    coords_blue = 255; 
		    		    coords_green = 255; 
		    		    coords_red = 255;
		    		}
				    int coordRGB = (coords_alpha<<24)|(coords_red<<16)|(coords_green<<8)|coords_blue;
				    bufImageCoords.setRGB(col, row, coordRGB);
		    		
		    	}
		    }


	    	File outputfile_coords = new File(outCoordsImage);
	    	boolean bSuccess_coords = ImageIO.write( bufImageCoords, "png", outputfile_coords);
	
		} catch (Exception e) {
			System.out.println("Exception e: " + e.toString());
		}

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		 JLabel labelWhite = new JLabel(new ImageIcon(bufImageWhite));
		 JLabel labelBlack = new JLabel(new ImageIcon(bufImageBlack));
		 JLabel labelCoords= new JLabel(new ImageIcon(bufImageCoords));

		JPanel panel = new JPanel();
		 panel.add(labelWhite);
		 panel.add(labelBlack);
		 panel.add(labelCoords);

		JScrollPane scrollPane = new JScrollPane(panel);

		scrollPane.setPreferredSize(new Dimension(1280, 370));

		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

	}

	private static BufferedImage loadImage(String fileName) {
		BufferedImage img = null;
		try {
			File tempFile = new File(fileName);
			img = ImageIO.read(tempFile);
			System.out.println("Read " + fileName);
		} catch (IOException e) {
			System.out.println("Could not read image: " + fileName);
		}

		return img;
	}

	private static byte[] accessBytes(BufferedImage image) {
		WritableRaster raster = image.getRaster();
		DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
		return buffer.getData();
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				solve();
			}
		});
	}
}
