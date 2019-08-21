package ysn.com.view.chrysanthemum;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import ysn.com.view.R;

/**
 * @Author yangsanning
 * @ClassName ChrysanthemumView
 * @Description 菊花View
 * @Date 2019/8/21
 * @History 2019/8/21 author: description:
 */
public class ChrysanthemumView extends View {

    /**
     * 线条开始颜色、线条结束颜色
     */
    private int startColor, endColor;

    /**
     * 线条个数 默认12条
     */
    private int lineCount = 12;

    /**
     * 背景画笔
     */
    private Paint bgPaint;

    /**
     * 渐变颜色
     */
    private int[] colors;

    private int viewWidth;
    private int viewHeight;

    /**
     * 线条长度
     */
    private int lineLength;

    /**
     * 线圆角及宽度
     */
    private int lineBold;

    /**
     * 动画是否已开启
     */
    private boolean isAnimationStart;

    /**
     * 开始index
     */
    private int startIndex;

    /**
     * 动画
     */
    private ValueAnimator valueAnimator;

    public ChrysanthemumView(Context context) {
        this(context, null);
    }

    public ChrysanthemumView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChrysanthemumView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initPaint();
        init();
    }

    /**
     * 初始化颜色
     */
    private void init() {
        // 渐变色计算类
        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        colors = new int[lineCount];
        // 获取对应的线颜色
        // 此处由于是白色起头 黑色结尾所以需要反过来计算 即线的数量到0的数量递减 对应的ValueAnimator 是从0到线的数量-1递增
        for (int i = lineCount; i > 0; i--) {
            float alpha = (float) i / lineCount;
            colors[lineCount - i] = (int) argbEvaluator.evaluate(alpha, startColor, endColor);
        }
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ChrysanthemumView);

        lineCount = array.getInt(R.styleable.ChrysanthemumView_cv_line_count, 12);
        startColor = array.getColor(R.styleable.ChrysanthemumView_cv_start_color,
                Color.parseColor("#FFFFFF"));
        endColor = array.getColor(R.styleable.ChrysanthemumView_cv_end_color,
                Color.parseColor("#00000000"));

        array.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStrokeJoin(Paint.Join.ROUND);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取view的宽度 默认40dp
        viewWidth = getViewSize(dp2px(getContext(), 40), widthMeasureSpec);
        // 获取view的高度 默认40dp
        viewHeight = getViewSize(dp2px(getContext(), 40), heightMeasureSpec);
        // 使宽高保持一致
        viewHeight = viewWidth = Math.min(viewWidth, viewHeight);
        // 获取线的长度
        lineLength = viewWidth / 6;
        // 获取线圆角及宽度
        lineBold = viewWidth / lineCount;
        // 设置线的圆角及宽度
        bgPaint.setStrokeWidth(lineBold);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    /**
     * MeasureSpec.UNSPECIFIED  父容器没有对当前View有任何限制，当前View可以任意取尺寸
     * MeasureSpec.AT_MOST      当前尺寸是当前View能取的最大尺寸
     * MeasureSpec.EXACTLY      当前的尺寸就是当前View应该取的尺寸
     *
     * @param defaultSize 默认大小
     * @param measureSpec 包含测量模式和宽高信息
     * @return 返回View的宽高大小
     */
    private int getViewSize(int defaultSize, int measureSpec) {
        int viewSize = defaultSize;
        //获取测量模式
        int mode = MeasureSpec.getMode(measureSpec);
        //获取大小
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                // 如果没有指定大小, 就设置为默认大小
                viewSize = defaultSize;
                break;
            case MeasureSpec.AT_MOST:
                // 如果测量模式是最大取值为size, 我们将大小取最大值,你也可以取其他值
                viewSize = size;
                break;
            case MeasureSpec.EXACTLY:
                // 如果是固定的大小, 那就不要去改变它
                viewSize = size;
                break;
            default:
        }
        return viewSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取半径
        int r = viewWidth / 2;
        // 绘制前先旋转一个角度, 使最顶上开始位置颜色与开始颜色匹配
        canvas.rotate(360f / lineCount, r, r);
        for (int i = 0; i < lineCount; i++) {
            // 获取颜色下标
            int index = (startIndex + i) % lineCount;
            // 设置颜色
            bgPaint.setColor(colors[index]);
            // 绘制线条 lineBold >> 1 == lineBold / 2 使居中显示
            canvas.drawLine(r, lineBold >> 1, r, (lineBold >> 1) + lineLength, bgPaint);
            // 旋转角度
            canvas.rotate(360f / lineCount, r, r);
        }
    }

    /**
     * 开始动画
     */
    public void startAnimation(int duration) {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofInt(lineCount, 0);
            valueAnimator.setDuration(duration);
            valueAnimator.setTarget(0);
            valueAnimator.setRepeatCount(-1);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // 此处会回调3次 需要去除后面的两次回调
                    if (startIndex != (int) animation.getAnimatedValue()) {
                        startIndex = (int) animation.getAnimatedValue();
                        invalidate();
                    }
                }
            });
        }
        valueAnimator.start();
        isAnimationStart = true;
    }

    /**
     * 开始动画 时间为1800毫秒一次
     */
    public void startAnimation() {
        startAnimation(1800);
    }

    /**
     * 结束动画
     */
    public void stopAnimation() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            isAnimationStart = false;
        }
    }

    /**
     * 是否在动画中
     *
     * @return 是为 true 否则 false
     */
    public boolean isAnimationStart() {
        return isAnimationStart;
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpVal   dpVal
     * @return px
     */

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * 防止内存溢出 未结束动画并退出页面时，需使用此函数，或手动释放此view
     */
    public void detachView() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
            isAnimationStart = false;
        }
    }

}