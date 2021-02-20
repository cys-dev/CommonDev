package com.cys.common.widget.loading;

import android.view.animation.Interpolator;

public class PathInterpolatorCompat implements Interpolator {

    // This governs how accurate the approximation of the Path is.
    private int count = 100;
    private float precision = 0.01f;

    private float[] mX; // x coordinates in the line
    private float[] mY; // y coordinates in the line

    /**
     * Create an interpolator for a cubic Bezier curve.  The end points
     * <code>(0, 0)</code> and <code>(1, 1)</code> are assumed.
     *
     * @param controlX1 The x coordinate of the first control point of the cubic Bezier.
     * @param controlY1 The y coordinate of the first control point of the cubic Bezier.
     * @param controlX2 The x coordinate of the second control point of the cubic Bezier.
     * @param controlY2 The y coordinate of the second control point of the cubic Bezier.
     */
    public PathInterpolatorCompat(float controlX1, float controlY1, float controlX2, float controlY2) {
        initPath(controlX1, controlY1, controlX2, controlY2);
    }

    public PathInterpolatorCompat(float controlX1, float controlY1, float controlX2, float controlY2, int duration) {
        int per = duration / 5;
        count = per > count ? per : count;
        precision = 1.0f / count;
        initPath(controlX1, controlY1, controlX2, controlY2);
    }

    /**
     * 根据贝赛尔曲线计算公式计算贝赛尔曲线上至少100个点的坐标
     */
    private void initPath(float x1, float y1, float x2, float y2) {
        mX = new float[count];
        mY = new float[count];
        float t = 0;

        //计算各常量值，尽可能减少循环中乘法计算次数
        float ax = 1 + 3 * x1 - 3 * x2;
        float bx = 3 * x2 - 6 * x1;
        float cx = 3 * x1;
        float ay = 1 + 3 * y1 - 3 * y2;
        float by = 3 * y2 - 6 * y1;
        float cy = 3 * y1;
        float tt;
        float ttt;
        for (int i = 0; i < count; i++) {
            tt = t * t;
            ttt = tt * t;
            mX[i] = ax * ttt + bx * tt + cx * t;
            mY[i] = ay * ttt + by * tt + cy * t;
            t += precision;
        }
    }

    /**
     * Using the line in the Path in this interpolator that can be described as
     * <code>y = f(x)</code>, finds the y coordinate of the line given <code>t</code>
     * as the x coordinate. Values less than 0 will always return 0 and values greater
     * than 1 will always return 1.
     *
     * @param t Treated as the x coordinate along the line.
     * @return The y coordinate of the Path along the line where x = <code>t</code>.
     * @see Interpolator#getInterpolation(float)
     */
    @Override
    public float getInterpolation(float t) {
        if (t <= 0) {
            return 0;
        } else if (t >= 1) {
            return 1;
        }
        // Do a binary search for the correct x to interpolate between.
        int startIndex = 0;
        int endIndex = mX.length - 1;

        while (endIndex - startIndex > 1) {
            int midIndex = (startIndex + endIndex) / 2;
            if (t < mX[midIndex]) {
                endIndex = midIndex;
            } else {
                startIndex = midIndex;
            }
        }

        float xRange = mX[endIndex] - mX[startIndex];
        if (xRange == 0) {
            return mY[startIndex];
        }

        float tInRange = t - mX[startIndex];
        float fraction = tInRange / xRange;

        float startY = mY[startIndex];
        float endY = mY[endIndex];
        return startY + (fraction * (endY - startY));
    }
}