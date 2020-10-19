package data;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

public class Utilitys {

	public static byte[] getImageData(MultipartFile file) {
		byte[]buffer = new byte[(int) file.getSize()];

        try {
         InputStream fileInputStream = file.getInputStream();//new FileInputStream(file);
        //convert file into array of bytes
         fileInputStream.read(buffer);
         fileInputStream.close();
        } catch (Exception e) {
         e.printStackTrace();
        }
        return buffer;
	}

	public static BufferedImage getBufferedImage(byte[] imgData ) throws IOException {
	    return ImageIO.read(new ByteArrayInputStream(imgData));
	}
	
	public static String getImgDataAs64String(byte[] byteData) {
		//Base64.getEncoder().encodeToString(fileContent)
        return Base64.getMimeEncoder().encodeToString(byteData);
    }
	public static byte[]  getImgDataTh(MultipartFile file)  {
		return getImgDataTh(file,100,100);
	}
	public static byte[]  getImgDataTh(MultipartFile file,  int width, int height)  {
		BufferedImage originalImage = null;
		try {
			originalImage = ImageIO.read(file.getInputStream());
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		if ((originalImage!=null) && (originalImage.getWidth()>width || originalImage.getHeight()>height)) {
			
			 try {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					BufferedImage thumbnail = Thumbnails.of(originalImage).size(width, height).asBufferedImage();
				 				 
			        ImageIO.write(thumbnail, "png", bos);
			        byte[] imageBytes = bos.toByteArray();
			        bos.close();
			        return imageBytes;			       
			       
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			 
			 return getImageData(file); //en cas d'erreur...
		}
		else
			return getImageData(file);
		
		
	}
	
	public static String getImgDataThAs64String(byte[] byteData,  int width, int height)  {
		BufferedImage originalImage = null;
		try {
			originalImage = getBufferedImage(byteData);
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		if ((originalImage!=null) && (originalImage.getWidth()>width || originalImage.getHeight()>height)) {
			
			 try {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					BufferedImage thumbnail = Thumbnails.of(originalImage).size(width, height).asBufferedImage();
				 				 
			        ImageIO.write(thumbnail, "png", bos);
			        byte[] imageBytes = bos.toByteArray();
			        bos.close();
			        return Base64.getMimeEncoder().encodeToString(imageBytes);			       
			       
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			 
			 return getImgDataAs64String(byteData); //en cas d'erreur...
		}
		else
			return getImgDataAs64String(byteData);
		
		
	}
	
	public static String getImgDataThAs64String(byte[] byteData)  {
		return getImgDataThAs64String(byteData, 100, 100);
//		BufferedImage originalImage = null;
//		try {
//			originalImage = getBufferedImage(byteData);
//		} catch (IOException e1) {
//			
//			e1.printStackTrace();
//		}
//		if ((originalImage!=null) && (originalImage.getWidth()>100 || originalImage.getHeight()>100)) {
//			
//			 try {
//					ByteArrayOutputStream bos = new ByteArrayOutputStream();
//					BufferedImage thumbnail = Thumbnails.of(originalImage).size(100, 100).asBufferedImage();
//				 				 
//			        ImageIO.write(thumbnail, "png", bos);
//			        byte[] imageBytes = bos.toByteArray();
//			        bos.close();
//			        return Base64.getMimeEncoder().encodeToString(imageBytes);			       
//			       
//			    } catch (IOException e) {
//			        e.printStackTrace();
//			    }
//			 
//			 return getImgDataAs64String(byteData); //en cas d'erreur...
//		}
//		else
//			return getImgDataAs64String(byteData);	
	}
	
	public static String encodeToString(BufferedImage image, String type) {
	    String imageString = null;
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();

	    try {
	        ImageIO.write(image, type, bos);
	        byte[] imageBytes = bos.toByteArray();

	        Base64.Encoder encoder = Base64.getEncoder();
	        imageString = encoder.encodeToString(imageBytes);

	        bos.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return imageString;
	}
}

//public static byte[] getImageData(String pathImage) {
//File file = new File(pathImage);
//byte[]bFile = new byte[(int) file.length()];
//
//try {
//    FileInputStream fileInputStream = new FileInputStream(file);
//   //convert file into array of bytes
//    fileInputStream.read(bFile);
//    fileInputStream.close();
//    return bFile;
//   } catch (Exception e) {
//    e.printStackTrace();
//   }
//return null;
//}