package engine.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.HashMap;

/*
 * This class not only loads assets, but also caches them
 *
 * this should get rid of those weird hitches in the gameplay when images are loaded and such
 */
public class AssetLoader {
    public static HashMap<String, Image> imageCache = new HashMap<>();
    public static HashMap<String, Image> audioCache = new HashMap<>();

    private static String[] imagesToPreload = {
            "check.png",
            "chicken.jpg"
    };

    private static String[] audioToPreload = {
            ""
    };

    /*
     * NOTE: Since this thing prepares the image, too, I don't think the same image can be
     * reused at different sizes...
     */
    public static Image loadAndPrepareImage(String filename, double width, double rWidth, double height, double rHeight) {
        if(imageCache.containsKey(filename)) {
            return imageCache.get(filename);
        }

        Image img = Toolkit.getDefaultToolkit().getImage(filename);
        while(!Toolkit.getDefaultToolkit().prepareImage(img, (int)(width * rWidth), (int)(height * rHeight), null)){}

        imageCache.put(filename, img);

        return img;
    }
}

