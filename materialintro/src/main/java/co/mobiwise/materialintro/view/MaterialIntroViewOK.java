package co.mobiwise.materialintro.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import co.mobiwise.materialintro.MaterialIntroConfiguration;
import co.mobiwise.materialintro.R;
import co.mobiwise.materialintro.animation.AnimationFactory;
import co.mobiwise.materialintro.animation.AnimationListener;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.prefs.PreferencesManager;
import co.mobiwise.materialintro.shape.Circle;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.Rect;
import co.mobiwise.materialintro.shape.Shape;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.target.Target;
import co.mobiwise.materialintro.target.ViewTarget;
import co.mobiwise.materialintro.utils.Constants;
import co.mobiwise.materialintro.utils.Utils;

/**
 * Created by mertsimsek on 22/01/16.
 * 解决单个的时候可以成功
 */
public class MaterialIntroViewOK extends RelativeLayout {
    public static final String TAG = MaterialIntroViewOK.class.getSimpleName();
    /**
     * Mask color
     */
    private int maskColor;

    /**
     * MaterialIntroView will start
     * showing after delayMillis seconds
     * passed
     */
    private long delayMillis;

    /**
     * We don't draw MaterialIntroView
     * until isReady field set to true
     */
    private boolean isReady;

    /**
     * Show/Dismiss MaterialIntroView
     * with fade in/out animation if
     * this is enabled.
     */
    private boolean isFadeAnimationEnabled;

    /**
     * Animation duration
     */
    private long fadeAnimationDuration;

    /**
     * targetShape focus on target
     * and clear circle to focus
     */
    private Shape targetShape;

    /**
     * Focus Type
     */
    private Focus focusType;

    /**
     * FocusGravity type
     */
    private FocusGravity focusGravity;

    /**
     * Target View
     */
    private Target targetView;

    /**
     * Eraser
     */
    private Paint eraser;

    /**
     * Handler will be used to
     * delay MaterialIntroView
     */
    private Handler handler;

    /**
     * All views will be drawn to
     * this bitmap and canvas then
     * bitmap will be drawn to canvas
     */
    private Bitmap bitmap;
    private Canvas canvas;

    /**
     * Circle padding
     */
    private int padding;

    /**
     * Layout width/height
     */
    private int width;
    private int height;

    /**
     * Dismiss on touch any position
     */
    private boolean dismissOnTouch;

    /**
     * Info dialog view
     */
    private View infoView;

    /**
     * Info dialog text color
     */
    private int colorTextViewInfo;

    /**
     * Info dialog will be shown
     * If this value true
     */
    private boolean isInfoEnabled;

    /**
     * Dot view will appear center of
     * cleared target area
     */
    private View dotView;

    /**
     * Dot View will be shown if
     * this is true
     */
    private boolean isDotViewEnabled;

    /**
     * Image View will be shown if
     * this is true
     */
    private boolean isImageViewEnabled;

    /**
     * Save/Retrieve status of MaterialIntroView
     * If Intro is already learnt then don't show
     * it again.
     */
    private PreferencesManager preferencesManager;

    /**
     * Check using this Id whether user learned
     * or not.
     */
    private String materialIntroViewId;

    /**
     * When layout completed, we set this true
     * Otherwise onGlobalLayoutListener stuck on loop.
     */
    private boolean isLayoutCompleted;

    /**
     * Notify user when MaterialIntroView is dismissed
     */
    private MaterialIntroListener materialIntroListener;

    /**
     * Perform click operation to target
     * if this is true
     */
    private boolean isPerformClick;

    /**
     * Disallow this MaterialIntroView from showing up more than once at a time
     */
    private boolean isIdempotent;

    /**
     * Shape of target
     */
    private ShapeType shapeType;

    /**
     * Use custom shape
     */
    private boolean usesCustomShape = false;
    private List<Integer[]> listRes = new ArrayList();
    private int currentStep = 0;
    private ImageView nextBtn;
    private ImageView introTips;
    private ImageView introJump;

    public MaterialIntroViewOK(Context context) {
        super(context);
        init(context);
    }

    public MaterialIntroViewOK(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MaterialIntroViewOK(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialIntroViewOK(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        setVisibility(INVISIBLE);

        listRes.add(new Integer[] {R.drawable.intro_tips1, R.drawable.intro_next1});
        listRes.add(new Integer[] {R.drawable.intro_tips2, R.drawable.intro_next1});
        listRes.add(new Integer[] {R.drawable.intro_tips3, R.drawable.intro_next1});
        listRes.add(new Integer[] {R.drawable.intro_tips4, R.drawable.intro_next2});

        /**
         * set default values
         */
        maskColor = Constants.DEFAULT_MASK_COLOR;
        delayMillis = Constants.DEFAULT_DELAY_MILLIS;
        fadeAnimationDuration = Constants.DEFAULT_FADE_DURATION;
        padding = Constants.DEFAULT_TARGET_PADDING;
        colorTextViewInfo = Constants.DEFAULT_COLOR_TEXTVIEW_INFO;
        focusType = Focus.ALL;
        focusGravity = FocusGravity.CENTER;
        shapeType = ShapeType.CIRCLE;
        isReady = false;
        isFadeAnimationEnabled = true;
        dismissOnTouch = false;
        isLayoutCompleted = false;
        isInfoEnabled = false;
        isDotViewEnabled = false;
        isPerformClick = false;
        isImageViewEnabled = true;
        isIdempotent = false;

        /**
         * initialize objects
         */
        handler = new Handler();

        preferencesManager = new PreferencesManager(context);

        eraser = new Paint();
        eraser.setColor(0xFFFFFFFF);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraser.setFlags(Paint.ANTI_ALIAS_FLAG);

        View layoutInfo = LayoutInflater.from(getContext()).inflate(R.layout.material_intro_card, null);

        infoView = layoutInfo.findViewById(R.id.info_layout);

        dotView = LayoutInflater.from(getContext()).inflate(R.layout.dotview, null);
        dotView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                targetShape.reCalculateAll();
                if (targetShape != null && targetShape.getPoint().y != 0 && !isLayoutCompleted) {
                    if (isInfoEnabled)
                        setInfoLayout();
                    setJumpViewLayout();
                    setTipsViewLayout();
                    setNextViewLayout();
                    removeOnGlobalLayoutListener(MaterialIntroViewOK.this, this);
                }
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isReady) return;

        if (bitmap == null || canvas == null) {
            if (bitmap != null) bitmap.recycle();

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            this.canvas = new Canvas(bitmap);
        }

        /**
         * Draw mask
         */
        this.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        this.canvas.drawColor(maskColor);

        /**
         * Clear focus area
         */
        targetShape.draw(this.canvas, eraser, padding);

        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    /**
     * Perform click operation when user
     * touches on target circle.
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xT = event.getX();
        float yT = event.getY();

        /*
        boolean isTouchOnFocus = targetShape.isTouchOnFocus(xT, yT);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (isTouchOnFocus && isPerformClick) {
                    targetView.getView().setPressed(true);
                    targetView.getView().invalidate();
                }

                return true;
            case MotionEvent.ACTION_UP:

                if (isTouchOnFocus || dismissOnTouch)
                    dismiss();

                if (isTouchOnFocus && isPerformClick) {
                    targetView.getView().performClick();
                    targetView.getView().setPressed(true);
                    targetView.getView().invalidate();
                    targetView.getView().setPressed(false);
                    targetView.getView().invalidate();
                }

                return true;
            default:
                break;
        }
        */

        return super.onTouchEvent(event);
    }

    /**
     * Shows material view with fade in
     * animation
     *
     * @param activity
     */
    private void show(Activity activity) {

        if (preferencesManager.isDisplayed(materialIntroViewId))
            return;

        ((ViewGroup) activity.getWindow().getDecorView()).addView(this);

        setReady(true);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFadeAnimationEnabled)
                    AnimationFactory.animateFadeIn(MaterialIntroViewOK.this, fadeAnimationDuration, new AnimationListener.OnAnimationStartListener() {
                        @Override
                        public void onAnimationStart() {
                            setVisibility(VISIBLE);
                        }
                    });
                else
                    setVisibility(VISIBLE);
            }
        }, delayMillis);

        if(isIdempotent) {
            preferencesManager.setDisplayed(materialIntroViewId);
        }
    }

    /**
     * Dismiss Material Intro View
     */
    public void dismiss() {
        if(!isIdempotent) {
            preferencesManager.setDisplayed(materialIntroViewId);
        }

        AnimationFactory.animateFadeOut(this, fadeAnimationDuration, new AnimationListener.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                setVisibility(GONE);
                removeMaterialView();

                if (materialIntroListener != null)
                    materialIntroListener.onUserClicked(materialIntroViewId);
            }
        });
    }

    private void removeMaterialView(){
        if(getParent() != null )
            ((ViewGroup) getParent()).removeView(this);
    }

    /**
     * locate info card view above/below the
     * circle. If circle's Y coordiante is bigger than
     * Y coordinate of root view, then locate cardview
     * above the circle. Otherwise locate below.
     */
    private void setInfoLayout() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                isLayoutCompleted = true;

                if (infoView.getParent() != null)
                    ((ViewGroup) infoView.getParent()).removeView(infoView);

                LayoutParams infoDialogParams = new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT);

                if (targetShape.getPoint().y < height / 2) {
                    ((RelativeLayout) infoView).setGravity(Gravity.TOP);
                    infoDialogParams.setMargins(
                            0,
                            targetShape.getPoint().y + targetShape.getHeight() / 2,
                            0,
                            0);
                } else {
                    ((RelativeLayout) infoView).setGravity(Gravity.BOTTOM);
                    infoDialogParams.setMargins(
                            0,
                            0,
                            0,
                            height - (targetShape.getPoint().y + targetShape.getHeight() / 2) + 2 * targetShape.getHeight() / 2);
                }

                infoView.setLayoutParams(infoDialogParams);
                infoView.postInvalidate();

                addView(infoView);
                infoView.setBackgroundColor(getContext().getResources().getColor(R.color.black));

                infoView.setVisibility(VISIBLE);
            }
        });
    }

    private void setJumpViewLayout() {

        handler.post(new Runnable() {
            @Override
            public void run() {

                if (introJump == null) {
                    introJump = new ImageView(getContext());
                    introJump.setScaleType(ImageView.ScaleType.CENTER);
                }

                introJump.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (introJump != null) {
                            dismiss();
                        }
                    }
                });

                if (introJump.getParent() != null)
                    ((ViewGroup) introJump.getParent()).removeView(introJump);

                LayoutParams dotViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                introJump.setImageResource(R.drawable.jump);

                int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                introJump.measure(w, h);
                int viewWidth = introJump.getMeasuredWidth();
                dotViewLayoutParams.setMargins(
                        getScreenWidth(getContext()) - viewWidth - Utils.dpToPx(16),
                        getStatusHeight(getContext()) + Utils.dpToPx(14),
                        0,
                        0);
                introJump.setLayoutParams(dotViewLayoutParams);
                introJump.postInvalidate();
                addView(introJump);

                introJump.setVisibility(VISIBLE);
            }
        });
    }

    private void setNextViewLayout() {

        handler.post(new Runnable() {
            @Override
            public void run() {

                if (nextBtn == null) {
                    nextBtn = new ImageView(getContext());
                    nextBtn.setScaleType(ImageView.ScaleType.CENTER);
                }

                nextBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (nextBtn != null && introTips != null) {
                            if (currentStep >= listRes.size() - 1) {
                                dismiss();
                            } else {
                                Integer[] resArray = listRes.get(currentStep + 1);
                                introTips.setImageResource(resArray[0]);
                                nextBtn.setImageResource(resArray[1]);
                            }
                            currentStep++;
                        }
                    }
                });

                if (nextBtn.getParent() != null)
                    ((ViewGroup) nextBtn.getParent()).removeView(nextBtn);

                LayoutParams dotViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                nextBtn.setImageResource(R.drawable.intro_next1);

                int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                nextBtn.measure(w, h);
                int viewWidth = nextBtn.getMeasuredWidth();
                int viewHeight = nextBtn.getMeasuredHeight();
                dotViewLayoutParams.setMargins(
                        targetShape.getPoint().x - (viewWidth / 2) + Utils.dpToPx(125),
                        targetShape.getPoint().y - (viewHeight / 2) + Utils.dpToPx(95),
                        0,
                        0);
                nextBtn.setLayoutParams(dotViewLayoutParams);
                nextBtn.postInvalidate();
                addView(nextBtn);

                nextBtn.setVisibility(VISIBLE);
            }
        });
    }

    private void setTipsViewLayout() {

        handler.post(new Runnable() {
            @Override
            public void run() {

                if (introTips == null) {
                    introTips = new ImageView(getContext());
                    introTips.setScaleType(ImageView.ScaleType.CENTER);
                }

                if (introTips.getParent() != null)
                    ((ViewGroup) introTips.getParent()).removeView(introTips);

                LayoutParams dotViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                introTips.setImageResource(R.drawable.intro_tips1);
                Log.e(TAG, targetShape.getPoint().x + "");
                Log.e(TAG, targetShape.getPoint().y + "");

                int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                introTips.measure(w, h);
                int viewWidth = introTips.getMeasuredWidth();
                int viewHeight = introTips.getMeasuredHeight();
                dotViewLayoutParams.setMargins(
                        targetShape.getPoint().x - (viewWidth / 2) + Utils.dpToPx(30),
                        targetShape.getPoint().y - (viewHeight / 2) - Utils.dpToPx(110),
                        0,
                        0);
                introTips.setLayoutParams(dotViewLayoutParams);
                introTips.postInvalidate();
                addView(introTips);

                introTips.setVisibility(VISIBLE);
            }
        });
    }

    /**
     * SETTERS
     */

    private void setMaskColor(int maskColor) {
        this.maskColor = maskColor;
    }

    private void setDelay(int delayMillis) {
        this.delayMillis = delayMillis;
    }

    private void enableFadeAnimation(boolean isFadeAnimationEnabled) {
        this.isFadeAnimationEnabled = isFadeAnimationEnabled;
    }

    private void setShapeType(ShapeType shape) {
        this.shapeType = shape;
    }

    private void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    private void setTarget(Target target) {
        targetView = target;
    }

    private void setFocusType(Focus focusType) {
        this.focusType = focusType;
    }

    private void setShape(Shape shape) {
        this.targetShape = shape;
    }

    private void setPadding(int padding) {
        this.padding = padding;
    }

    private void setDismissOnTouch(boolean dismissOnTouch) {
        this.dismissOnTouch = dismissOnTouch;
    }

    private void setFocusGravity(FocusGravity focusGravity) {
        this.focusGravity = focusGravity;
    }

    private void enableInfoDialog(boolean isInfoEnabled) {
        this.isInfoEnabled = isInfoEnabled;
    }

    private void enableImageViewIcon(boolean isImageViewEnabled){
        this.isImageViewEnabled = isImageViewEnabled;
    }

    private void setIdempotent(boolean idempotent){
        this.isIdempotent = idempotent;
    }

    private void enableDotView(boolean isDotViewEnabled){
        this.isDotViewEnabled = isDotViewEnabled;
    }

    public void setConfiguration(MaterialIntroConfiguration configuration) {

        if (configuration != null) {
            this.maskColor = configuration.getMaskColor();
            this.delayMillis = configuration.getDelayMillis();
            this.isFadeAnimationEnabled = configuration.isFadeAnimationEnabled();
            this.colorTextViewInfo = configuration.getColorTextViewInfo();
            this.isDotViewEnabled = configuration.isDotViewEnabled();
            this.dismissOnTouch = configuration.isDismissOnTouch();
            this.colorTextViewInfo = configuration.getColorTextViewInfo();
            this.focusType = configuration.getFocusType();
            this.focusGravity = configuration.getFocusGravity();
        }
    }

    private void setUsageId(String materialIntroViewId) {
        this.materialIntroViewId = materialIntroViewId;
    }

    private void setListener(MaterialIntroListener materialIntroListener) {
        this.materialIntroListener = materialIntroListener;
    }

    private void setPerformClick(boolean isPerformClick){
        this.isPerformClick = isPerformClick;
    }

    /**
     * Builder Class
     */
    public static class Builder {

        private MaterialIntroViewOK materialIntroView;

        private Activity activity;

        private Focus focusType = Focus.MINIMUM;

        public Builder(Activity activity) {
            this.activity = activity;
            materialIntroView = new MaterialIntroViewOK(activity);
        }

        public Builder setMaskColor(int maskColor) {
            materialIntroView.setMaskColor(maskColor);
            return this;
        }

        public Builder setDelayMillis(int delayMillis) {
            materialIntroView.setDelay(delayMillis);
            return this;
        }

        public Builder enableFadeAnimation(boolean isFadeAnimationEnabled) {
            materialIntroView.enableFadeAnimation(isFadeAnimationEnabled);
            return this;
        }

        public Builder setShape(ShapeType shape) {
            materialIntroView.setShapeType(shape);
            return this;
        }

        public Builder setFocusType(Focus focusType) {
            materialIntroView.setFocusType(focusType);
            return this;
        }

        public Builder setFocusGravity(FocusGravity focusGravity) {
            materialIntroView.setFocusGravity(focusGravity);
            return this;
        }

        public Builder setTarget(View view) {
            materialIntroView.setTarget(new ViewTarget(view));
            return this;
        }

        public Builder setTargetPadding(int padding) {
            materialIntroView.setPadding(padding);
            return this;
        }

        public Builder dismissOnTouch(boolean dismissOnTouch) {
            materialIntroView.setDismissOnTouch(dismissOnTouch);
            return this;
        }

        public Builder setUsageId(String materialIntroViewId) {
            materialIntroView.setUsageId(materialIntroViewId);
            return this;
        }

        public Builder enableDotAnimation(boolean isDotAnimationEnabled) {
            materialIntroView.enableDotView(isDotAnimationEnabled);
            return this;
        }

        public Builder enableIcon(boolean isImageViewIconEnabled) {
            materialIntroView.enableImageViewIcon(isImageViewIconEnabled);
            return this;
        }

        public Builder setIdempotent(boolean idempotent) {
            materialIntroView.setIdempotent(idempotent);
            return this;
        }

        public Builder setConfiguration(MaterialIntroConfiguration configuration) {
            materialIntroView.setConfiguration(configuration);
            return this;
        }

        public Builder setListener(MaterialIntroListener materialIntroListener) {
            materialIntroView.setListener(materialIntroListener);
            return this;
        }

        public Builder setCustomShape(Shape shape) {
            materialIntroView.usesCustomShape = true;
            materialIntroView.setShape(shape);
            return this;
        }

        public Builder performClick(boolean isPerformClick){
            materialIntroView.setPerformClick(isPerformClick);
            return this;
        }

        public MaterialIntroViewOK build() {
            if(materialIntroView.usesCustomShape) {
                return materialIntroView;
            }

            // no custom shape supplied, build our own
            Shape shape;

            if(materialIntroView.shapeType == ShapeType.CIRCLE) {
                shape = new Circle(
                        materialIntroView.targetView,
                        materialIntroView.focusType,
                        materialIntroView.focusGravity,
                        materialIntroView.padding);
            } else {
                shape = new Rect(
                        materialIntroView.targetView,
                        materialIntroView.focusType,
                        materialIntroView.focusGravity,
                        materialIntroView.padding);
            }

            materialIntroView.setShape(shape);
            return materialIntroView;
        }

        public MaterialIntroViewOK show() {
            build().show(activity);
            return materialIntroView;
        }

    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }
}
