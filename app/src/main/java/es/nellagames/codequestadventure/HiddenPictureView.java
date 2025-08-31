package es.nellagames.codequestadventure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HiddenPictureView extends View {

    private static final int[] IMAGE_RESOURCES = {
            R.drawable.bird, R.drawable.cabin, R.drawable.dino,
            R.drawable.dog, R.drawable.dragon, R.drawable.fox,
            R.drawable.panda, R.drawable.play, R.drawable.robot,
            R.drawable.unicorn
    };

    private Bitmap hiddenPicture;
    private Bitmap maskBitmap;
    private Canvas maskCanvas;
    private Paint revealPaint, coverPaint, gridPaint, borderPaint, textPaint;
    private List<RectF> puzzlePieces = new ArrayList<>();
    private int revealedPieces = 0;
    private int totalPieces = 10;
    private int currentImageResource;
    private String currentImageName;
    private String currentImageDescription;
    private Random random = new Random();

    public HiddenPictureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        selectRandomImage();
        loadPicture();

        revealPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        revealPaint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));

        coverPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        coverPaint.setColor(Color.parseColor("#BDBDBD"));

        gridPaint = new Paint();
        gridPaint.setColor(Color.GREEN);
        gridPaint.setStrokeWidth(3);
        gridPaint.setAlpha(150);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.GREEN);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(6);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setShadowLayer(2, 2, 2, Color.BLACK);

        puzzlePieces.clear();
    }

    private void selectRandomImage() {
        int idx = random.nextInt(IMAGE_RESOURCES.length);
        currentImageResource = IMAGE_RESOURCES[idx];
        currentImageName = ""; // Simplified
        currentImageDescription = "";
    }

    private void loadPicture() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), currentImageResource);
        if (bitmap != null) {
            hiddenPicture = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
            if (bitmap != hiddenPicture) bitmap.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);

        if (w>0 && h>0) {
            maskBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
            maskCanvas = new Canvas(maskBitmap);
            maskCanvas.drawColor(Color.parseColor("#BDBDBD"));

            createPuzzlePieces(w,h);
            updateMask();
        }
    }

    private void createPuzzlePieces(int w, int h) {
        puzzlePieces.clear();
        float pieceWidth = w/5f;
        float pieceHeight = h/2f;
        for (int row=0; row<2; row++){
            for(int col=0; col<5; col++){
                puzzlePieces.add(new RectF(col*pieceWidth, row*pieceHeight, (col+1)*pieceWidth, (row+1)*pieceHeight));
            }
        }
    }

    public void revealPieces(int count) {
        revealedPieces = Math.min(count, totalPieces);
        updateMask();
        invalidate();
    }

    public void updateMask() {
        if (maskCanvas == null) return;

        maskCanvas.drawColor(Color.parseColor("#BDBDBD"));
        for (int i=0; i<revealedPieces && i<puzzlePieces.size(); i++){
            maskCanvas.drawRect(puzzlePieces.get(i), revealPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        canvas.drawColor(Color.parseColor("#E0F2F1"));
        canvas.drawRect(4,4,getWidth()-4,getHeight()-4,borderPaint);

        if (hiddenPicture!=null && maskBitmap!=null){
            RectF dest = new RectF(10,10,getWidth()-10,getHeight()-10);
            canvas.drawBitmap(hiddenPicture,null,dest,null);

            canvas.drawBitmap(maskBitmap,0,0,null);

            // Draw gridlines
            for(int i=0; i<revealedPieces && i<puzzlePieces.size();i++){
                canvas.drawRect(puzzlePieces.get(i), gridPaint);
            }
        }
    }

    public int getTotalPieces() {
        return totalPieces;
    }

    public void setTotalPieces(int totalPieces) {
        this.totalPieces = totalPieces;
        if (revealedPieces > totalPieces){
            revealedPieces = totalPieces;
            updateMask();
            invalidate();
        }
    }

    public int getRevealedPieces() {
        return revealedPieces;
    }

    public boolean isComplete() {
        return revealedPieces >= totalPieces;
    }
}

