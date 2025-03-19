package com.festival.dailypostermaker.PhotoEditor;

import android.graphics.PointF;


class Vector2D extends PointF {
    public static float getAngle(Vector2D vector2D, Vector2D vector2D2) {
        vector2D.normalize();
        vector2D2.normalize();
        return (float) ((Math.atan2(vector2D2.y, vector2D2.x) - Math.atan2(vector2D.y, vector2D.x)) * 57.29577951308232d);
    }

    public void normalize() {
        float sqrt = (float) Math.sqrt((this.x * this.x) + (this.y * this.y));
        this.x /= sqrt;
        this.y /= sqrt;
    }
}
