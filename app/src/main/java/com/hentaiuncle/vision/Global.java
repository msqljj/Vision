package com.hentaiuncle.vision;

import com.hentaiuncle.vision.model.operator.Binaryzation;
import com.hentaiuncle.vision.model.operator.BinaryzationRGB;
import com.hentaiuncle.vision.model.operator.Blur;
import com.hentaiuncle.vision.model.operator.Cameo;
import com.hentaiuncle.vision.model.operator.Edge3x3;
import com.hentaiuncle.vision.model.operator.Edge5x5;
import com.hentaiuncle.vision.model.operator.Grey;
import com.hentaiuncle.vision.model.operator.GreyEx;
import com.hentaiuncle.vision.model.operator.Inverse;
import com.hentaiuncle.vision.model.operator.Laplacian;
import com.hentaiuncle.vision.model.operator.Light;
import com.hentaiuncle.vision.model.operator.LowPoly;
import com.hentaiuncle.vision.model.operator.Sharp5x5;
import com.hentaiuncle.vision.model.operator.Sobel;

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
