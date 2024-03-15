package packWork;

import java.awt.image.BufferedImage;

interface Interfata {

    public String Get_path();

    public void Set_path(String path);

    public BufferedImage Get_img();

    public void Set_img(BufferedImage img);

    public void AdjustBrightness(int factor);

}