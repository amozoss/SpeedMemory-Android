package willoughby.com.speedmemory.views;



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

import willoughby.com.speedmemory.model.Card;
import willoughby.com.speedmemory.R;



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
  private ArrayList<Card>   mCards;
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
    mPaintFont.setColor(Color.WHITE);
    mPaintFont.setTextSize(40);
    Paint.FontMetrics fm = new Paint.FontMetrics();
    mPaintFont.setTextAlign(Paint.Align.CENTER);
    mPaintFont.getFontMetrics(fm);
    mCards = new ArrayList<Card>();
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
      for(Card card : mCards) {
        if (card.getRect().contains(touchX, touchY)) {
          Log.d("TOUCHRECT", String.format("Card %d, %d", card.getX(), card.getY()));
          if (mBoardViewDelegate != null) {
            mBoardViewDelegate.choose(card.getY(), card.getX());
          }
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

    int height = (mHeight / ROWS) - PADDING;
    int width = (mWidth / COLS) - PADDING;
    int count = 0;
    mCards.clear();
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
            mCards.add(new Card(rect, row, col));
            int colorNum = mBoardViewDelegate.getColor(row, col);
            int alpha = mBoardViewDelegate.getAlpha(row, col);
            int color = getColor(alpha, colorNum);
            mPaint.setColor(color);

            mCanvas.drawRect(rect, mPaint);
            mPaintFont.setAlpha(alpha);
            mCanvas.drawText(number, rect.exactCenterX(), rect.exactCenterY() + 15, mPaintFont);
          }
        }
      }
    }


    invalidate();
}

  private int getColor(int alpha, int colorNum) {
    int color;
    if (alpha == 255) {
      switch (colorNum) {
        case -1:
          color = Color.BLACK;
        case 0:
          color = Color.BLACK;
          break;
        case 1:
          color = Color.GREEN;
          break;
        case 2:
          color = Color.BLUE;
          break;
        case 3:
          color = Color.RED;
          break;
        case 4:
          color = Color.CYAN;
          break;
        case 5:
          color = Color.MAGENTA;
          break;
        case 6:
          color = Color.DKGRAY;
          break;
        default:
          color = Color.BLACK;
          break;
      }
    }
    else {
      color = Color.BLACK;
    }
    return color;
  }



  // region Getter & Setters
  public void setBoardViewDelegate(BoardViewDelegate mBoardViewDelegate) {
    this.mBoardViewDelegate = mBoardViewDelegate;
  }
  // endregion


  public interface BoardViewDelegate {
    String get(int row, int col);

    int getAlpha(int row, int col);

    int getColor(int row, int col);

    void choose(int row, int col);
  }
}
