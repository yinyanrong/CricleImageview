package view.custom.yyr.com.ycricleimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class CricleImageView extends View {

    private static final int TYPE_CIRCLE = 0;//cricle
    private static final int TYPE_ROUND = 1;//bound
    private int type;//绘制的类型
    private boolean  mShowBorder;//是否显示边框
    private int  mBorderWidth=10;//边框的宽度
    private int  mBorderColor =0xffff6600;//边框的颜色
    private Bitmap bitmap;
    private int mBorderRadius=50;//边角的弧度

    int mWidth;//控件的宽
    int mHeight;//控件的搞
    public CricleImageView(Context context) {
        this(context, null);
    }

    public CricleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CricleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CricleImageView, defStyleAttr, 0);

        int length = typedArray.length();//获取属性的个数

        //循环遍历下标
        for (int i = 0; i < length; i++) {

            int atrs = typedArray.getIndex(i);
                /**
                 *   报错Resource IDs cannot be used in a switch statement in Android library modules
                 *   switch  改成  if
                 */
              if(atrs== R.styleable.CricleImageView_borderRadius){
                  //获取值 直接转换为dp
                  mBorderRadius = typedArray.getDimensionPixelSize(atrs, (int) TypedValue.applyDimension(
                          TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
                }else if(atrs== R.styleable.CricleImageView_type){

                  type = typedArray.getInt(atrs, 0);
              }
              else if(atrs== R.styleable.CricleImageView_src){
                  //获取图片地址  创建Bitmap
                  bitmap = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(atrs, 0));
              }
              else if(atrs== R.styleable.CricleImageView_showBorder){
                  mShowBorder = typedArray.getBoolean(atrs, true);
              }
              else if(atrs== R.styleable.CricleImageView_borderColor){
                  mBorderColor=typedArray.getColor(atrs,0xffff6600);

              }
              else  if(atrs== R.styleable.CricleImageView_borderWidth){
                  mBorderWidth= typedArray.getDimensionPixelSize(atrs, (int) TypedValue.applyDimension(
                          TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
              }
        }
        //释放对象
        typedArray.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        //确定宽的值
        if (widthSpecMode == MeasureSpec.EXACTLY) {//match_parent
            mWidth = widthSpecSize;
        } else {
            int picWidth = getPaddingLeft() + getPaddingRight() + bitmap.getWidth();

            if (widthSpecMode == MeasureSpec.AT_MOST) {//wrap_content

                mWidth = Math.min(picWidth, widthSpecSize);
                //对比数组中的最小值
            } else {

                mWidth = picWidth;
            }
        }

        //确定高的值
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            mHeight = heightSpecSize;
        } else {
            int picHeigth = getPaddingBottom() + getPaddingTop() + bitmap.getHeight();

            if (heightMeasureSpec == MeasureSpec.AT_MOST) {
                mHeight = Math.min(picHeigth, heightSpecSize);
            } else {
                mHeight = picHeigth;
            }
        }
        setMeasuredDimension(mWidth, mHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (type) {

            case TYPE_CIRCLE:
                int  min=Math.min(mWidth,mHeight);

                 Bitmap  bitmap= createCricleImageView(min);

                canvas.drawBitmap(bitmap,0,0,null);
            break;

            case TYPE_ROUND:
                Bitmap  bitmap1= createBorderCriCleImageView();
                canvas.drawBitmap(bitmap1,0,0,null);
            break;
        }

    }
    //绘制圆形
    private Bitmap  createCricleImageView(int  min){
        Bitmap bitM =  Bitmap.createBitmap(min,min, Bitmap.Config.ARGB_8888);
        Paint  paint=new Paint();
        //设置抗锯齿
        paint.setAntiAlias(true);
        //创建空白的画布
        Canvas canV=new Canvas();
        //设置bitmap图形
        canV.setBitmap(bitM);
        //绘制圆形
        canV.drawCircle(min/2,min/2,min/2,paint);
        //PorterDuff.Mode.SRC_IN   取两层绘制交集。显示上层。
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //如果图片过大那么 将进行压缩成bitM 一样的大小
        bitmap= Bitmap.createScaledBitmap(bitmap,min,min,false);
        //把压缩过的图片通过Canvas绘制到bitmap中
        canV.drawBitmap(bitmap,0,0,paint);
        if(mShowBorder){
            Paint  pa=new Paint();
            pa.setAntiAlias(true);
            pa.setStrokeWidth(mBorderWidth);
            pa.setColor(mBorderColor);
            pa.setStyle(Paint.Style.STROKE);
            //半径 - 画笔宽度的/2
            canV.drawCircle(min/2,min/2,min/2-mBorderWidth/2,pa);
        }
        return  bitM;
    }
    //绘制图片为圆角
    private Bitmap  createBorderCriCleImageView(){
        Paint  paint=new Paint();
        paint.setAntiAlias(true);
        Bitmap  bitM=  Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        Canvas canV=new Canvas();
        canV.setBitmap(bitM);
        RectF  rectF =new RectF(0,0,mWidth,mHeight);
        canV.drawRoundRect(rectF,mBorderRadius,mBorderRadius,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        bitmap= Bitmap.createScaledBitmap(bitmap,mWidth,mHeight,false);
        canV.drawBitmap(bitmap,0,0,paint);
        return  bitM;
    }
}
