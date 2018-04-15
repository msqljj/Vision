package us.xingkong.vision;

import us.xingkong.vision.model.operator.Binaryzation;
import us.xingkong.vision.model.operator.BinaryzationRGB;
import us.xingkong.vision.model.operator.Blur;
import us.xingkong.vision.model.operator.BlurEx;
import us.xingkong.vision.model.operator.Cameo;
import us.xingkong.vision.model.operator.Edge3x3;
import us.xingkong.vision.model.operator.Edge5x5;
import us.xingkong.vision.model.operator.Grey;
import us.xingkong.vision.model.operator.GreyEx;
import us.xingkong.vision.model.operator.ImageOperator;
import us.xingkong.vision.model.operator.Inverse;
import us.xingkong.vision.model.operator.Laplacian;
import us.xingkong.vision.model.operator.Light;
import us.xingkong.vision.model.operator.LowPoly;
import us.xingkong.vision.model.operator.Sharp5x5;
import us.xingkong.vision.model.operator.Sobel;

/**
 * Created by Hansin on 2018/4/13.
 */

public class Global {
    public static Class<?>[] OPERATORS = {
            Blur.class,
            Grey.class,
            GreyEx.class,
            Inverse.class,
            Light.class,
            LowPoly.class,
            null,
            Binaryzation.class,
            BinaryzationRGB.class,
            null,
            Sobel.class,
            Edge3x3.class,
            Edge5x5.class,
            Sharp5x5.class,
            Laplacian.class,
            Cameo.class
    };

    public static String[] PERMISSIONS = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
}
