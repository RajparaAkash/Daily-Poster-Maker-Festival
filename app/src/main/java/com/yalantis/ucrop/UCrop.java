package com.yalantis.ucrop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yalantis.ucrop.model.AspectRatio;

import java.util.ArrayList;
import java.util.Arrays;

public class UCrop {

    public static final int REQUEST_CROP = 69;
    public static final int RESULT_ERROR = 96;
    public static final int MIN_SIZE = 10;

    private static final String EXTRA_PREFIX = "com.yalantis.ucrop";

    public static final String EXTRA_INPUT_URI = EXTRA_PREFIX + ".InputUri";
    public static final String EXTRA_OUTPUT_URI = EXTRA_PREFIX + ".OutputUri";
    public static final String EXTRA_OUTPUT_CROP_ASPECT_RATIO = EXTRA_PREFIX + ".CropAspectRatio";
    public static final String EXTRA_OUTPUT_IMAGE_WIDTH = EXTRA_PREFIX + ".ImageWidth";
    public static final String EXTRA_OUTPUT_IMAGE_HEIGHT = EXTRA_PREFIX + ".ImageHeight";
    public static final String EXTRA_OUTPUT_OFFSET_X = EXTRA_PREFIX + ".OffsetX";
    public static final String EXTRA_OUTPUT_OFFSET_Y = EXTRA_PREFIX + ".OffsetY";
    public static final String EXTRA_ERROR = EXTRA_PREFIX + ".Error";

    public static final String EXTRA_ASPECT_RATIO_X = EXTRA_PREFIX + ".AspectRatioX";
    public static final String EXTRA_ASPECT_RATIO_Y = EXTRA_PREFIX + ".AspectRatioY";

    public static final String EXTRA_MAX_SIZE_X = EXTRA_PREFIX + ".MaxSizeX";
    public static final String EXTRA_MAX_SIZE_Y = EXTRA_PREFIX + ".MaxSizeY";

    private Intent mCropIntent;
    private Bundle mCropOptionsBundle;

    public static UCrop of(@NonNull Uri source, @NonNull Uri destination) {
        return new UCrop(source, destination);
    }

    private UCrop(@NonNull Uri source, @NonNull Uri destination) {
        mCropIntent = new Intent();
        mCropOptionsBundle = new Bundle();
        mCropOptionsBundle.putParcelable(EXTRA_INPUT_URI, source);
        mCropOptionsBundle.putParcelable(EXTRA_OUTPUT_URI, destination);
    }

    public UCrop withAspectRatio(float x, float y) {
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_X, x);
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_Y, y);
        return this;
    }

    public UCrop useSourceImageAspectRatio() {
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_X, 0);
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_Y, 0);
        return this;
    }

    public UCrop withMaxResultSize(@IntRange(from = MIN_SIZE) int width, @IntRange(from = MIN_SIZE) int height) {
        if (width < MIN_SIZE) {
            width = MIN_SIZE;
        }

        if (height < MIN_SIZE) {
            height = MIN_SIZE;
        }

        mCropOptionsBundle.putInt(EXTRA_MAX_SIZE_X, width);
        mCropOptionsBundle.putInt(EXTRA_MAX_SIZE_Y, height);
        return this;
    }

    public UCrop withOptions(@NonNull Options options) {
        mCropOptionsBundle.putAll(options.getOptionBundle());
        return this;
    }

    public void start(@NonNull Activity activity) {
        start(activity, REQUEST_CROP);
    }

    public void start(@NonNull Activity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    public void start(@NonNull Context context, @NonNull Fragment fragment) {
        start(context, fragment, REQUEST_CROP);
    }

    public void start(@NonNull Context context, @NonNull Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(context), requestCode);
    }

    public void start(@NonNull Context context,
                      @NonNull ActivityResultLauncher<Intent> activityResultLauncher) {
        activityResultLauncher.launch(getIntent(context));
    }

    public Intent getIntent(@NonNull Context context) {
        mCropIntent.setClass(context, UCropActivity.class);
        mCropIntent.putExtras(mCropOptionsBundle);
        return mCropIntent;
    }

    public UCropFragment getFragment() {
        return UCropFragment.newInstance(mCropOptionsBundle);
    }

    public UCropFragment getFragment(Bundle bundle) {
        mCropOptionsBundle = bundle;
        return getFragment();
    }

    @Nullable
    public static Uri getOutput(@NonNull Intent intent) {
        return intent.getParcelableExtra(EXTRA_OUTPUT_URI);
    }

    public static int getOutputImageWidth(@NonNull Intent intent) {
        return intent.getIntExtra(EXTRA_OUTPUT_IMAGE_WIDTH, -1);
    }

    public static int getOutputImageHeight(@NonNull Intent intent) {
        return intent.getIntExtra(EXTRA_OUTPUT_IMAGE_HEIGHT, -1);
    }

    public static float getOutputCropAspectRatio(@NonNull Intent intent) {
        return intent.getFloatExtra(EXTRA_OUTPUT_CROP_ASPECT_RATIO, 0f);
    }

    @Nullable
    public static Throwable getError(@NonNull Intent result) {
        return (Throwable) result.getSerializableExtra(EXTRA_ERROR);
    }

    public static class Options {

        public static final String EXTRA_COMPRESSION_FORMAT_NAME = EXTRA_PREFIX + ".CompressionFormatName";
        public static final String EXTRA_COMPRESSION_QUALITY = EXTRA_PREFIX + ".CompressionQuality";

        public static final String EXTRA_ALLOWED_GESTURES = EXTRA_PREFIX + ".AllowedGestures";

        public static final String EXTRA_MAX_BITMAP_SIZE = EXTRA_PREFIX + ".MaxBitmapSize";
        public static final String EXTRA_MAX_SCALE_MULTIPLIER = EXTRA_PREFIX + ".MaxScaleMultiplier";
        public static final String EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION = EXTRA_PREFIX + ".ImageToCropBoundsAnimDuration";

        public static final String EXTRA_DIMMED_LAYER_COLOR = EXTRA_PREFIX + ".DimmedLayerColor";
        public static final String EXTRA_CIRCLE_DIMMED_LAYER = EXTRA_PREFIX + ".CircleDimmedLayer";

        public static final String EXTRA_SHOW_CROP_FRAME = EXTRA_PREFIX + ".ShowCropFrame";
        public static final String EXTRA_CROP_FRAME_COLOR = EXTRA_PREFIX + ".CropFrameColor";
        public static final String EXTRA_CROP_FRAME_STROKE_WIDTH = EXTRA_PREFIX + ".CropFrameStrokeWidth";

        public static final String EXTRA_SHOW_CROP_GRID = EXTRA_PREFIX + ".ShowCropGrid";
        public static final String EXTRA_CROP_GRID_ROW_COUNT = EXTRA_PREFIX + ".CropGridRowCount";
        public static final String EXTRA_CROP_GRID_COLUMN_COUNT = EXTRA_PREFIX + ".CropGridColumnCount";
        public static final String EXTRA_CROP_GRID_COLOR = EXTRA_PREFIX + ".CropGridColor";
        public static final String EXTRA_CROP_GRID_STROKE_WIDTH = EXTRA_PREFIX + ".CropGridStrokeWidth";

        public static final String EXTRA_TOOL_BAR_COLOR = EXTRA_PREFIX + ".ToolbarColor";
        public static final String EXTRA_STATUS_BAR_COLOR = EXTRA_PREFIX + ".StatusBarColor";
        public static final String EXTRA_UCROP_COLOR_CONTROLS_WIDGET_ACTIVE = EXTRA_PREFIX + ".UcropColorControlsWidgetActive";

        public static final String EXTRA_UCROP_WIDGET_COLOR_TOOLBAR = EXTRA_PREFIX + ".UcropToolbarWidgetColor";
        public static final String EXTRA_UCROP_TITLE_TEXT_TOOLBAR = EXTRA_PREFIX + ".UcropToolbarTitleText";
        public static final String EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE = EXTRA_PREFIX + ".UcropToolbarCancelDrawable";
        public static final String EXTRA_UCROP_WIDGET_CROP_DRAWABLE = EXTRA_PREFIX + ".UcropToolbarCropDrawable";

        public static final String EXTRA_UCROP_LOGO_COLOR = EXTRA_PREFIX + ".UcropLogoColor";

        public static final String EXTRA_HIDE_BOTTOM_CONTROLS = EXTRA_PREFIX + ".HideBottomControls";
        public static final String EXTRA_FREE_STYLE_CROP = EXTRA_PREFIX + ".FreeStyleCrop";

        public static final String EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT = EXTRA_PREFIX + ".AspectRatioSelectedByDefault";
        public static final String EXTRA_ASPECT_RATIO_OPTIONS = EXTRA_PREFIX + ".AspectRatioOptions";

        public static final String EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR = EXTRA_PREFIX + ".UcropRootViewBackgroundColor";


        private final Bundle mOptionBundle;

        public Options() {
            mOptionBundle = new Bundle();
        }

        @NonNull
        public Bundle getOptionBundle() {
            return mOptionBundle;
        }

        public void setCompressionFormat(@NonNull Bitmap.CompressFormat format) {
            mOptionBundle.putString(EXTRA_COMPRESSION_FORMAT_NAME, format.name());
        }

        public void setCompressionQuality(@IntRange(from = 0) int compressQuality) {
            mOptionBundle.putInt(EXTRA_COMPRESSION_QUALITY, compressQuality);
        }

        public void setAllowedGestures(@UCropActivity.GestureTypes int tabScale,
                                       @UCropActivity.GestureTypes int tabRotate,
                                       @UCropActivity.GestureTypes int tabAspectRatio) {
            mOptionBundle.putIntArray(EXTRA_ALLOWED_GESTURES, new int[]{tabScale, tabRotate, tabAspectRatio});
        }

        public void setMaxScaleMultiplier(@FloatRange(from = 1.0, fromInclusive = false) float maxScaleMultiplier) {
            mOptionBundle.putFloat(EXTRA_MAX_SCALE_MULTIPLIER, maxScaleMultiplier);
        }

        public void setImageToCropBoundsAnimDuration(@IntRange(from = MIN_SIZE) int durationMillis) {
            mOptionBundle.putInt(EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, durationMillis);
        }

        public void setMaxBitmapSize(@IntRange(from = MIN_SIZE) int maxBitmapSize) {
            mOptionBundle.putInt(EXTRA_MAX_BITMAP_SIZE, maxBitmapSize);
        }

        public void setDimmedLayerColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_DIMMED_LAYER_COLOR, color);
        }

        public void setCircleDimmedLayer(boolean isCircle) {
            mOptionBundle.putBoolean(EXTRA_CIRCLE_DIMMED_LAYER, isCircle);
        }

        public void setShowCropFrame(boolean show) {
            mOptionBundle.putBoolean(EXTRA_SHOW_CROP_FRAME, show);
        }

        public void setCropFrameColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_CROP_FRAME_COLOR, color);
        }

        public void setCropFrameStrokeWidth(@IntRange(from = 0) int width) {
            mOptionBundle.putInt(EXTRA_CROP_FRAME_STROKE_WIDTH, width);
        }

        public void setShowCropGrid(boolean show) {
            mOptionBundle.putBoolean(EXTRA_SHOW_CROP_GRID, show);
        }

        public void setCropGridRowCount(@IntRange(from = 0) int count) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_ROW_COUNT, count);
        }

        public void setCropGridColumnCount(@IntRange(from = 0) int count) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_COLUMN_COUNT, count);
        }

        public void setCropGridColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_COLOR, color);
        }

        public void setCropGridStrokeWidth(@IntRange(from = 0) int width) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_STROKE_WIDTH, width);
        }

        public void setToolbarColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_TOOL_BAR_COLOR, color);
        }

        public void setStatusBarColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_STATUS_BAR_COLOR, color);
        }

        public void setActiveControlsWidgetColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_COLOR_CONTROLS_WIDGET_ACTIVE, color);
        }

        public void setToolbarWidgetColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_WIDGET_COLOR_TOOLBAR, color);
        }

        public void setToolbarTitle(@Nullable String text) {
            mOptionBundle.putString(EXTRA_UCROP_TITLE_TEXT_TOOLBAR, text);
        }

        public void setToolbarCancelDrawable(@DrawableRes int drawable) {
            mOptionBundle.putInt(EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE, drawable);
        }

        public void setToolbarCropDrawable(@DrawableRes int drawable) {
            mOptionBundle.putInt(EXTRA_UCROP_WIDGET_CROP_DRAWABLE, drawable);
        }

        public void setLogoColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_LOGO_COLOR, color);
        }

        public void setHideBottomControls(boolean hide) {
            mOptionBundle.putBoolean(EXTRA_HIDE_BOTTOM_CONTROLS, hide);
        }

        public void setFreeStyleCropEnabled(boolean enabled) {
            mOptionBundle.putBoolean(EXTRA_FREE_STYLE_CROP, enabled);
        }

        public void setAspectRatioOptions(int selectedByDefault, AspectRatio... aspectRatio) {
            if (selectedByDefault >= aspectRatio.length) {
                return;
            }
            mOptionBundle.putInt(EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, selectedByDefault);
            mOptionBundle.putParcelableArrayList(EXTRA_ASPECT_RATIO_OPTIONS, new ArrayList<Parcelable>(Arrays.asList(aspectRatio)));
        }

        public void setRootViewBackgroundColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR, color);
        }

        public void withAspectRatio(float x, float y) {
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_X, x);
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_Y, y);
        }

        public void useSourceImageAspectRatio() {
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_X, 0);
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_Y, 0);
        }

        public void withMaxResultSize(@IntRange(from = MIN_SIZE) int width, @IntRange(from = MIN_SIZE) int height) {
            mOptionBundle.putInt(EXTRA_MAX_SIZE_X, width);
            mOptionBundle.putInt(EXTRA_MAX_SIZE_Y, height);
        }

    }

}
