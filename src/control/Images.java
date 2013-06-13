package control;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Images
{
	private static Map<String, BufferedImage> map = new HashMap<String, BufferedImage>();
	
	public static BufferedImage get(String name)
	{
        if (!map.containsKey(name))
        {
            try
            {
                map.put(name, ImageIO.read(Images.class.getResource("/view/img/" + name)));
                
            } catch (IOException e)
            {
                return null;
            }
        }

		return map.get(name);
	}
}
