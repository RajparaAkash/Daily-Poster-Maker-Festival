package com.festival.dailypostermaker.PageIndicatorView.draw.drawer.type;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.festival.dailypostermaker.PageIndicatorView.animation.data.Value;
import com.festival.dailypostermaker.PageIndicatorView.animation.data.type.SlideAnimationValue;
import com.festival.dailypostermaker.PageIndicatorView.draw.data.Indicator;
import com.festival.dailypostermaker.PageIndicatorView.draw.data.Orientation;

public class SlideDrawer extends BaseDrawer {

    public SlideDrawer(@NonNull Paint paint, @NonNull Indicator indicator) {
        super(paint, indicator);
    }

    public void draw(
            @NonNull Canvas canvas,
            @NonNull Value value,
            int coordinateX,
            int coordinateY) {

        if (!(value instanceof SlideAnimationValue)) {
            return;
        }

        SlideAnimationValue v = (SlideAnimationValue) value;
        int coordinate = v.getCoordinate();
        int unselectedColor = indicator.getUnselectedColor();
        int selectedColor = indicator.getSelectedColor();
        int radius = indicator.getRadius();

        paint.setColor(unselectedColor);
        canvas.drawCircle(coordinateX, coordinateY, radius, paint);

        paint.setColor(selectedColor);
        if (indicator.getOrientation() == Orientation.HORIZONTAL) {
            canvas.drawCircle(coordinate, coordinateY, radius, paint);
        } else {
            canvas.drawCircle(coordinateX, coordinate, radius, paint);
        }
    }
}
