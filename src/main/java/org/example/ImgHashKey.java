package org.example;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import javax.print.attribute.Size2DSyntax;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;

public class ImgHashKey {
    public static String getImgHashKey(Mat frame) {
        List<Mat> bgrChannels = new ArrayList<>();
        Core.split(frame, bgrChannels);
        int heightMid = frame.height() / 2;
        int widthMid = frame.width() / 2;
        int shiftCnt = 33;
        BigInteger shiftInt = new BigInteger("0");
        double sum = 0;
        for (int i = 0; i < 4; i++) {
            int rowStartIdx = (i / 2 * heightMid);
            int rowEndIdx = rowStartIdx + heightMid;
            int colStartIdx = (i / 2 * widthMid);
            int colEndIdx = colStartIdx + widthMid;
            Mat targetMat = new Mat();
            for (int j = 0; j < 3; j++) {
                if (j % 3 == 0) {
                    targetMat = bgrChannels.get(2); // Red channel
                } else if (j % 3 == 1) {
                    targetMat = bgrChannels.get(1); // Green channel
                } else if (j % 3 == 2) {
                    targetMat = bgrChannels.get(0); // Blue channel
                }

                int step = 2;
                Mat subMat = targetMat.submat(rowStartIdx, rowEndIdx, colStartIdx, colEndIdx);
                Mat resultMat = new Mat(subMat.rows(), subMat.cols(), subMat.type());
                for (int row = 0; row < subMat.rows(); row += step) {
                    for (int col = 0; col < subMat.cols(); col += step) {
                        double[] pixel = subMat.get(row, col);
                        resultMat.put(row, col, pixel[0] / 32);
                    }
                }

                for (int row = 0; row < resultMat.rows(); row++) {
                    for (int col = 0; col < resultMat.cols(); col++) {
                        double[] pixel = resultMat.get(row, col);
                        sum += pixel[0];
                    }
                }
                int count = targetMat.rows() * targetMat.cols();
                if (count == 1) {
                    shiftInt = shiftInt.add(BigInteger.valueOf(0));
                } else {
                    int addValue = (int) (sum / count);
                    BigInteger bigInteger = BigInteger.valueOf(addValue);
                    shiftInt = shiftInt.add(bigInteger);
                    shiftInt = shiftInt.shiftLeft(shiftCnt);
                }
                shiftCnt -= 3;
            }
        }
        String rgbResultHex = String.format("%032x", shiftInt);
        return rgbResultHex;
    }
}
