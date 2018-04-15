package us.xingkong.vision.model.operator;

import vision.core.Operator;

/**
 * 模糊
 *
 * Created by 饶翰新 on 2018/4/13.
 */
public class Sharp5x5 extends Convolve5x5 {


    public Sharp5x5(){
        super();
        kerlen = Operator.Sharp;
    }


    @Override
    public String OperatorName() {
        return "锐化";
    }
}
