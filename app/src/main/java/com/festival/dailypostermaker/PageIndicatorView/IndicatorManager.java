package com.festival.dailypostermaker.PageIndicatorView;

import androidx.annotation.Nullable;

import com.festival.dailypostermaker.PageIndicatorView.animation.AnimationManager;
import com.festival.dailypostermaker.PageIndicatorView.animation.controller.ValueController;
import com.festival.dailypostermaker.PageIndicatorView.animation.data.Value;
import com.festival.dailypostermaker.PageIndicatorView.draw.DrawManager;
import com.festival.dailypostermaker.PageIndicatorView.draw.data.Indicator;

public class IndicatorManager implements ValueController.UpdateListener {

    private DrawManager drawManager;
    private AnimationManager animationManager;
    private Listener listener;

    interface Listener {
        void onIndicatorUpdated();
    }

    IndicatorManager(@Nullable Listener listener) {
        this.listener = listener;
        this.drawManager = new DrawManager();
        this.animationManager = new AnimationManager(drawManager.indicator(), this);
    }

    public AnimationManager animate() {
        return animationManager;
    }

    public Indicator indicator() {
        return drawManager.indicator();
    }

    public DrawManager drawer() {
        return drawManager;
    }

    @Override
    public void onValueUpdated(@Nullable Value value) {
        drawManager.updateValue(value);
        if (listener != null) {
            listener.onIndicatorUpdated();
        }
    }
}
