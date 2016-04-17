package steganography;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import javax.imageio.ImageIO;

class dowork
{
	BufferedImage image,afterEncryptionImage;
	int height,width,count=0,red,green,blue,size;
	short encryptedStringLength;
	File input;
	int[][] pixeldata;
	int[] pixels;
	Color c;
	String inputString,encryptedString;
	byte[] stringBytes;
	WritableRaster raster;
	
	void getImageAndDimension() throws IOException
	{
		input=new File("1.jpg");
		image = ImageIO.read(input);
		width= image.getWidth();
		height = image.getHeight();
		size = height * width;

	}
	void createPixelData()
	{
		pixeldata = new int[size][3];
		for(int i = 0; i < height;i++)
		{
			for(int j = 0;j < width;j++)
			{
				c = new Color(image.getRGB(j, i));
				red = c.getRed();
				green = c.getGreen();
				blue = c.getBlue();
				pixeldata[count][0]=red;
				pixeldata[count][1]=green;
				pixeldata[count][2]=blue;
				count++;
			}
		}
	}
	void printPixelData()
	{
		for(int i=0;i<10;i++)
		{
			for(int j=0;j<3;j++)
			{
				System.out.print(pixeldata[i][j] + "  ");
			}
			System.out.println("");
			
		}
	}
	void getString()
	{
		@SuppressWarnings("resource")
		Scanner scanString = new Scanner(System.in);
		System.out.println("Enter String to encrypt : ");
		inputString = scanString.nextLine();
	}
	void stringEncrptionWithTDES() throws Exception
	{
		TripleDESTest DESobj = new TripleDESTest();
		encryptedString = DESobj._encrypt(inputString, "SecretKey");
		//System.out.println(encryptedString);
	}
	void getByteEquivalentOfString() throws UnsupportedEncodingException
	{
		stringBytes = encryptedString.getBytes("US-ASCII");
		/*for(int i=0;i<stringBytes.length;i++)
		{
			System.out.print(stringBytes[i]+"\t");
		}*/
	}
	void manipulatePixelData()
	{
		encryptedStringLength = (short) encryptedString.length();
		short len = encryptedStringLength;
		//first 16 bits of size
		short k;
		int row=0,col=0;
		for(int i =0;i<16;i++)
		{
			k = (short) (len&1);
			if((k==1)&&((pixeldata[row][col]&1)==0))
			{
				pixeldata[row][col]+=1;
			}
			else if((k==0)&&((pixeldata[row][col]&1)==1))
			{
				pixeldata[row][col]^=1;
			}
			if(col==2)
			{
				col=0;
				row++;
			}
			else
				col++;
			len>>=1;
			
		}
		//complete encrypted string
		for(byte bit:stringBytes)
		{
			len = bit;
			for(int i = 0;i<8;i++)
			{
				k = (short) (len&1);
				if((k==1)&&((pixeldata[row][col]&1)==0))
				{
					pixeldata[row][col]+=1;
				}
				else if((k==0)&&((pixeldata[row][col]&1)==1))
				{
					pixeldata[row][col]^=1;
				}
				if(col==2)
				{
					col=0;
					row++;
				}
				else
					col++;
				len>>=1;
			}
		}
		
	}
	void createPixels()
	{
		count = 0;
		pixels = new int[size];
		for(int i=0;i<size;i++)
		{
			pixels[i]=(pixeldata[i][0]<<16)|(pixeldata[i][1]<<8)|(pixeldata[i][2]);
		}
		for(int i=0;i<10;i++)
		{
			System.out.println(pixels[i]);
		}
	}
	void createImageFromPixel() throws IOException

	{
		afterEncryptionImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		raster = afterEncryptionImage.getRaster();
		//raster = (WritableRaster) afterEncryptionImage.getData();
		raster.setDataElements(0, 0, width, height, pixels);
		File f=new File("2.jpg");
		ImageIO.write(afterEncryptionImage, "jpg", f);
		
	}
}

public class Pixel1 {

	public static void main(String[] args) throws Exception {
		
		dowork obj = new dowork();
		obj.getImageAndDimension();
		obj.createPixelData();
		obj.printPixelData();
		obj.getString();
		obj.stringEncrptionWithTDES();
		obj.getByteEquivalentOfString();
		obj.manipulatePixelData();
		System.out.println("after manipulation------------------------------");
		obj.printPixelData();
		obj.createPixels();
		obj.createImageFromPixel();
		

	}

}
