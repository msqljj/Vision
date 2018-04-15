package us.xingkong.vision.model.operator;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import us.xingkong.vision.ScriptC_grey;
import vision.core.Core;
import vision.image.VisionImage;

/**
 * 明暗处理
 *
 * Created by 饶翰新 on 2018/4/13.
 */

public class Light extends ImageOperator {

    public int d;

    public Light(){
        super();
        d = 10;
        setArgText("d","系数");

    }

    @Override
    public void Operator(VisionImage img, Context context) {
        Core.Light(img,d/10.0f);
    }

    @Override
    public String OperatorName() {
        return "明暗处理";
    }
}
