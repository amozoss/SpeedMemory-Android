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
  private static final int DEFAULT_ROWS = 10;
  private static final int DEFAULT_COLS = 10;
  private static final int DEFAULT_PADDING = 1;

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
  private int mRows;
  private int mCols;
  private int mPadding;
  private boolean mIsCleared;


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
    mIsCleared = false;
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
    setMeasuredDimension(mWidth, mHeight);

    mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
    mCanvas = new Canvas(mBitmap);
    drawBoard();
  }




  private void drawBoard() {
    int padding = getPadding();
    int rows = getRows();
    int cols = getCols();

    int height = (mHeight / rows) - padding;
    int width = (mWidth / cols) - padding;
    int count = 0;
    mCards.clear();
    if (mIsCleared) {
      mCanvas.drawColor(Color.WHITE);
    }
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        Rect rect = new Rect(col * width + ((col + 1) * padding),
                             row * height + ((row + 1) * padding),
                             (col + 1) * width + (col * padding),
                             (row + 1) * height + (row * padding));
        if (mBoardViewDelegate != null) {
          String number = mBoardViewDelegate.get(row, col);
          if ("".equals(number)) {
            mCanvas.drawRect(rect, mPaintPurple);
          } else {
            mCards.add(new Card(rect, row, col));
            String colorNum = mBoardViewDelegate.getColor(row, col);
            int alpha = mBoardViewDelegate.getAlpha(row, col);
            int color = Color.parseColor(colorNum);
            mPaint.setColor(color);

            mCanvas.drawRect(rect, mPaint);
            mPaintFont.setAlpha(alpha);
            mCanvas.drawText(number, rect.exactCenterX(), rect.exactCenterY() + 15, mPaintFont);
          }
        }
      }
    }
  }


  // region Getter & Setters
  public void setBoardViewDelegate(BoardViewDelegate mBoardViewDelegate) {
    this.mBoardViewDelegate = mBoardViewDelegate;
  }

  public boolean isCleared() {
    return mIsCleared;
  }

  public void setIsCleared(boolean isCleared) {
    this.mIsCleared = isCleared;
  }

  public int getCols() {
    return (mCols == 0 ? DEFAULT_COLS : mCols);
  }


  public void setCols(int mCols) {
    this.mCols = mCols;
  }


  public int getPadding() {
    return (mPadding == 0 ? DEFAULT_PADDING : mPadding);
  }


  public void setPadding(int mPadding) {
    this.mPadding = mPadding;
  }


  public int getRows() {
    return (mRows == 0 ? DEFAULT_ROWS : mRows);
  }


  public void setRows(int mRows) {
    this.mRows = mRows;
  }
  // endregion


  public interface BoardViewDelegate {

    String get(int row, int col);

    int getAlpha(int row, int col);

    String getColor(int row, int col);

    void choose(int row, int col);
  }
}
