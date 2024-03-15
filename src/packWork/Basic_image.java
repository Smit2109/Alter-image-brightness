package packWork;

import java.awt.image.BufferedImage;

public abstract class Basic_image implements Interfata{

    private BufferedImage Img;

    public void Set_img(BufferedImage img) { this.Img=img; }
    public BufferedImage Get_img() { return Img;}
    public void AdjustBrightness(int factor) {}

}