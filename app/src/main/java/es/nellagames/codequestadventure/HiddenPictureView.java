package es.nellagames.codequestadventure.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import es.nellagames.codequestadventure.R;
import java.util.ArrayList;
import java.util.List;

public class HiddenPictureView extends View {

    private Bitmap hiddenPicture;
    private Bitmap maskBitmap;
    private Canvas maskCanvas;
    private Paint revealPaint, coverPaint, textPaint, borderPaint;
    private List<RectF> puzzlePieces;
    private int revealedPieces = 0;
    private int totalPieces = 10;

    public HiddenPictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    private void initializeView() {
        // Load the hidden picture (create a simple robot drawing if image not available)
        try {
            hiddenPicture = BitmapFactory.decodeResource(getResources(), R.drawable.hidden_robot);
        } catch (Exception e) {
            // Create a simple colored rectangle as fallback
            hiddenPicture = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(hiddenPicture);
            Paint tempPaint = new Paint();
            tempPaint.setColor(Color.parseColor("#4CAF50"));
            tempCanvas.drawRect(0, 0, 400, 400, tempPaint);

            // Draw a simple robot
            tempPaint.setColor(Color.parseColor("#2196F3"));
            tempCanvas.drawRect(150, 100, 250, 200, tempPaint); // head
            tempCanvas.drawRect(100, 200, 300, 350, tempPaint); // body

            tempPaint.setColor(Color.WHITE);
            tempCanvas.drawCircle(170, 130, 15, tempPaint); // left eye
            tempCanvas.drawCircle(230, 130, 15, tempPaint); // right eye

            tempPaint.setColor(Color.BLACK);
            tempCanvas.drawCircle(170, 130, 8, tempPaint); // left pupil
            tempCanvas.drawCircle(230, 130, 8, tempPaint); // right pupil
        }

        // Initialize paints
        revealPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        revealPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        coverPaint = new Paint();
        coverPaint.setColor(Color.parseColor("#BDBDBD"));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setShadowLayer(2, 2, 2, Color.BLACK);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.parseColor("#4CAF50"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(6f);

        // Create puzzle pieces
        puzzlePieces = new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Create mask bitmap
        maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        maskCanvas = new Canvas(maskBitmap);
        maskCanvas.drawColor(Color.parseColor("#BDBDBD")); // Initially covered

        createPuzzlePieces();
        updateMask();
    }

    private void createPuzzlePieces() {
        puzzlePieces.clear();

        if (getWidth() <= 0 || getHeight() <= 0) return;

        // Create 10 puzzle pieces (2 rows x 5 columns)
        float pieceWidth = getWidth() / 5f;
        float pieceHeight = getHeight() / 2f;

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                float left = col * pieceWidth;
                float top = row * pieceHeight;
                float right = left + pieceWidth;
                float bottom = top + pieceHeight;

                RectF piece = new RectF(left, top, right, bottom);
                puzzlePieces.add(piece);
            }
        }
    }

    public void revealPieces(int numPieces) {
        revealedPieces = Math.min(numPieces, totalPieces);
        updateMask();
        invalidate();
    }

    public void revealNextPiece() {
        if (revealedPieces < totalPieces) {
            revealedPieces++;
            updateMask();
            invalidate();
        }
    }

    private void updateMask() {
        if (maskCanvas == null || puzzlePieces.isEmpty()) return;

        // Clear mask and redraw covered state
        maskCanvas.drawColor(Color.parseColor("#BDBDBD"));

        // Reveal pieces based on progress
        for (int i = 0; i < revealedPieces && i < puzzlePieces.size(); i++) {
            RectF piece = puzzlePieces.get(i);
            maskCanvas.drawRect(piece, revealPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background
        canvas.drawColor(Color.parseColor("#E8EAF6"));

        // Draw border
        canvas.drawRect(5, 5, getWidth() - 5, getHeight() - 5, borderPaint);

        if (hiddenPicture != null && maskBitmap != null) {
            // Scale and draw the hidden picture
            RectF destRect = new RectF(10, 10, getWidth() - 10, getHeight() - 10);
            canvas.drawBitmap(hiddenPicture, null, destRect, null);

            // Draw the mask on top
            canvas.drawBitmap(maskBitmap, 0, 0, null);

            // Draw puzzle grid lines for revealed pieces
            Paint gridPaint = new Paint();
            gridPaint.setColor(Color.parseColor("#4CAF50"));
            gridPaint.setStrokeWidth(3f);
            gridPaint.setAlpha(150);

            for (int i = 0; i < revealedPieces && i < puzzlePieces.size(); i++) {
                RectF piece = puzzlePieces.get(i);
                canvas.drawRect(piece, gridPaint);
            }
        }

        // Draw progress text
        String progressText = "🖼️ Imagen: " + revealedPieces + "/" + totalPieces;
        canvas.drawText(progressText, getWidth() / 2f, getHeight() - 30, textPaint);

        // Draw completion message
        if (revealedPieces >= totalPieces) {
            Paint completePaint = new Paint(textPaint);
            completePaint.setTextSize(36);
            completePaint.setColor(Color.parseColor("#FFD700"));
            canvas.drawText("¡IMAGEN COMPLETA! 🎉", getWidth() / 2f, 50, completePaint);
        }
    }

    public int getRevealedPieces() {
        return revealedPieces;
    }

    public boolean isComplete() {
        return revealedPieces >= totalPieces;
    }
}
