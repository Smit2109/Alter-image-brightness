package packWork;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Manipulation extends Image{
    public Manipulation(String... readPaths) {
        super();
        for (String path : readPaths) {
            System.out.println(readPaths);
        }
    }

    public void read() throws IOException{
        Set_img(ImageIO.read(new File(this.Get_path())));
    }

    public void read(String path) throws IOException{
        try {
            Set_path(path);
            Set_img(ImageIO.read(new File(path)));
        } catch (IOException e) {
            System.err.println("Error: File not found or cannot be read.");
            System.exit(1); // Terminate the program
        }

    }

    public void write(Manipulation img) throws IOException{
        ImageIO.write(img.Get_img(),"bmp",new File("modified_" + this.Get_path()));
    }

    public int[] widthAndHeight() {
        BufferedImage image=this.Get_img();
        int[] dimensions = new int[2];
        dimensions[0] = image.getWidth();
        dimensions[1] = image.getHeight();
        return dimensions;
    }

    public void AdjustBrightness(int factor) {
        BufferedImage image=this.Get_img();
        int width=image.getWidth();
        int height=image.getHeight();				//citire lungime si latimea imaginii

        for(int i=0;i<height;i++) {
            for(int j=0;j<width;j++) {				//o bucla care ia toti pixelii in imagine
                Color c = new Color(image.getRGB(j, i));

                int r = c.getRed()   + factor;
                int g = c.getGreen() + factor;				//aplicarea factorului de modificare asupra componentelor pixelului
                int b = c.getBlue()  + factor;

                if (r >= 256) {
                    r = 255;
                } else if (r < 0) {
                    r = 0;
                }

                if (g >= 256) {								//verifica daca o componenta este in intervalul [0,255]
                    g = 255;									//daca valoarea dupa modificare depaseste intervalul acesta
                } else if (g < 0) {							//este setat la 0 sau 255
                    g = 0;
                }

                if (b >= 256) {
                    b = 255;
                } else if (b < 0) {
                    b = 0;
                }

                image.setRGB(j, i,new Color(r,g,b).getRGB());		//setarea noilor valori
            }
        }
    }
}