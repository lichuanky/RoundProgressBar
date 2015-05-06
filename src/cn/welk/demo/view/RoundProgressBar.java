package cn.welk.demo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import cn.welk.demo.R;

public class RoundProgressBar extends View {

    private final DisplayMetrics displayMetrics;

    private int maxProgress = 1000;
    private int progress = 0;
    private int borderWidth;

    private int externalColor;
    private int internalColor;
    private int progressColor;
    private int duration;

    private RectF oval;
    private Paint paint;
    private Bitmap progressIcon;
    private Matrix matrix;

    public RoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();

        oval = new RectF();
        paint = new Paint();
        matrix = new Matrix();

        internalColor = getResources().getColor(R.color.default_internal_color);
        externalColor = getResources().getColor(R.color.default_external_color);
        progressColor = getResources().getColor(R.color.default_progress_color);
        maxProgress = getResources().getInteger(R.integer.default_max);
        borderWidth = getResources().getDimensionPixelSize(R.dimen.default_border_width);

        TypedArray attributes = context.obtainStyledAttributes(
                attrs, R.styleable.RoundProgressBar);
        initAttributes(attributes);
        initPadding();
    }

    private void initAttributes(TypedArray attr) {
        externalColor = attr.getColor(R.styleable.RoundProgressBar_external_color,
                externalColor);
        internalColor = attr.getColor(R.styleable.RoundProgressBar_internal_color,
                internalColor);
        progressColor = attr.getColor(R.styleable.RoundProgressBar_progress_color,
                progressColor);
        maxProgress = attr.getInt(R.styleable.RoundProgressBar_max, maxProgress);
        duration = attr.getInt(R.styleable.RoundProgressBar_duration, duration);
        borderWidth = attr.getDimensionPixelSize(R.styleable.RoundProgressBar_border_width, borderWidth) ;
        progressIcon = BitmapFactory.decodeResource(getResources(),
                attr.getResourceId(R.styleable.RoundProgressBar_progress_icon, -1));
    }

    private void initPadding() {
        int left, top, right, bottom;
        int iconWidth = progressIcon == null ? 0 : progressIcon.getWidth();
        int padding = iconWidth > borderWidth ? (iconWidth - borderWidth) / 2 : 0;

        left = getPaddingLeft() > iconWidth ? getPaddingLeft() : padding;
        top = getPaddingTop() > iconWidth ? getPaddingTop() : left;
        right = getPaddingRight() > iconWidth ? getPaddingRight() : left;
        bottom = getPaddingBottom() > iconWidth ? getPaddingBottom() : left;

        setPadding(left, top, right, bottom);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        // Anti-Aliasing
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawColor(Color.TRANSPARENT);

        oval.left = borderWidth + getPaddingTop();
        oval.top = borderWidth + getPaddingTop();
        oval.right = width - borderWidth - getPaddingTop();
        oval.bottom = height - borderWidth - getPaddingTop();

        paint.setColor(internalColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawArc(oval, -90, 360, false, paint);

        oval.left = borderWidth / 2 + getPaddingTop();
        oval.top = borderWidth / 2 + getPaddingTop();
        oval.right = width - borderWidth / 2 - getPaddingTop();
        oval.bottom = height - borderWidth / 2 - getPaddingTop();

        paint.setAntiAlias(true);
        paint.setColor(externalColor);
        paint.setStrokeWidth(borderWidth);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawArc(oval, -90, 360, false, paint);
        paint.setColor(progressColor);
        float angle = ((float) progress / maxProgress) * 360;
        canvas.drawArc(oval, -90, angle, false, paint);

        if (progressIcon != null) {
            canvas.translate(getWidth() / 2, getWidth() / 2);
            float distance = (oval.width() - progressIcon.getWidth()) / 2;
            distance = distance / 1.6f;
            matrix.setTranslate(distance, distance);
            matrix.postRotate(-135 + angle);
            canvas.drawBitmap(progressIcon, matrix, null);
        }
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        this.invalidate();
    }

    public void setProgressNotInUiThread(int progress) {
        this.progress = progress;
        this.postInvalidate();
    }
}
