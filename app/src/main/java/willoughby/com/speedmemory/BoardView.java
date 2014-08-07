package willoughby.com.speedmemory;



import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;



/**
 * Created by dan on 7/19/14.
 */
public class BoardView extends View {

  private Context mContext;
  private int     mWidth;
  private int     mHeight;
  private Bitmap  mBitmap;
  private Canvas  mCanvas;
  Paint mPaint;
  Paint mPaintPurple;
  Paint mPaintFont;
  private ArrayList<Rect> mRects;
  private BoardViewDelegate mBoardViewDelegate;


  public BoardView(Context context) {
    super(context);
    init(context);
  }


  public BoardView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }


  public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }


  public void init(Context context) {
    mPaint = new Paint();
    mPaint.setColor(Color.BLACK);
    mPaint.setStyle(Paint.Style.FILL);
    mPaintPurple = new Paint();
    int myColor = context.getResources().getColor(R.color.light_purple);
    mPaintPurple.setColor(myColor);
    mPaintPurple.setStyle(Paint.Style.FILL);
    mPaintFont = new Paint();
    mPaintFont.setColor(Color.GREEN);
    mPaintFont.setTextSize(40);
    Paint.FontMetrics fm = new Paint.FontMetrics();
    mPaintFont.setTextAlign(Paint.Align.CENTER);
    mPaintFont.getFontMetrics(fm);
    mRects = new ArrayList<Rect>();
    mContext = context;
  }


  @Override
  protected void onDraw(Canvas canvas) {
    drawBoard();
    canvas.drawBitmap(mBitmap, 0, 0, mPaint);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    int touchX = (int) event.getX();
    int touchY = (int) event.getY();
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      for(Rect rect : mRects) {
        if (rect.contains(touchX, touchY)) {
          Log.d("RECT", String.format("Rect %d, %d", rect.centerX(), rect.centerY()));
        }
      }
    }
    return true;
  }


  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    mHeight = View.MeasureSpec.getSize(heightMeasureSpec);
    mWidth = View.MeasureSpec.getSize(widthMeasureSpec);


    //width = Math.min(width, height);
    //height = Math.min(width,height);


    //MUST CALL THIS
    setMeasuredDimension(mWidth, mHeight);

    mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
    mCanvas = new Canvas(mBitmap);
    drawBoard();
  }


  /*
  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    Log.i("VolumeGauge", "Size changed to " + w + "x" + h);

    // This is where you divide up the view into 64
    // pieces and paint them.
    generateBackground();
  }
  */


  final static int ROWS = 10;
  final static int COLS = 10;
  final static int PADDING = 1;

  private void drawBoard() {

    int width = (mWidth / ROWS) - PADDING;
    int height = (mHeight / COLS) - PADDING;
    int count = 0;
    //mRects.clear();
    for (int row = 0; row < ROWS ; row++) {
      for (int col = 0; col < COLS; col++) {
        Rect rect = new Rect(col * width + ((col + 1) * PADDING),
                 row * height + ((row + 1) * PADDING),
                 (col + 1) * width + (col * PADDING),
                 (row + 1) * height + (row * PADDING));
        if (mBoardViewDelegate != null) {
          String number = mBoardViewDelegate.get(row, col);
          if ("".equals(number)) {
            mCanvas.drawRect(rect, mPaintPurple);
          }
          else {
            //mRects.add(rect);
            mCanvas.drawRect(rect, mPaint);
            mCanvas.drawText(number, rect.exactCenterX(), rect.exactCenterY() + 15, mPaintFont);
          }
        }
      }
    }


    invalidate();
}


  private void generateBackground() {
      /*
      this.removeAllViews();

      int total = 12;
      int column = 5;
      int row = total / column;
      this.setColumnCount(column);
      this.setRowCount(row + 1);
      for(int i =0, c = 0, r = 0; i < total; i++, c++)
      {
        if(c == column)
        {
          c = 0;
          r++;
        }
        View oImageView = new View(this.getContext());
        oImageView.setBackgroundColor(Color.RED);
        GridLayout.LayoutParams param =new GridLayout.LayoutParams();
        param.height =  10;
        param.width = 10;
        param.rightMargin = 5;
        param.topMargin = 5;
        param.setGravity(Gravity.CENTER);
        param.columnSpec = GridLayout.spec(c);
        param.rowSpec = GridLayout.spec(r);
        oImageView.setLayoutParams (param);
        this.addView(oImageView);
      }
      */

  }

  // region Getter & Setters
  public void setBoardViewDelegate(BoardViewDelegate mBoardViewDelegate) {
    this.mBoardViewDelegate = mBoardViewDelegate;
  }
  // endregion


  public interface BoardViewDelegate {
    String get(int row, int col);
  }
}
