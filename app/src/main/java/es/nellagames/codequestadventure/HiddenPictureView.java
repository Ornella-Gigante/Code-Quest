package es.nellagames.codequestadventure;

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
        // Crear imagen programáticamente (sin necesidad de archivo)
        createHiddenPictureImage();

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

    private void createHiddenPictureImage() {
        // Crear un bitmap de 400x400 píxeles
        hiddenPicture = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(hiddenPicture);

        // Fondo degradado
        Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.parseColor("#E8F5E8"));
        tempCanvas.drawRect(0, 0, 400, 400, backgroundPaint);

        // Dibujar un robot simpático
        Paint robotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Cabeza del robot
        robotPaint.setColor(Color.parseColor("#4A90E2"));
        tempCanvas.drawRoundRect(new RectF(120, 80, 280, 200), 20, 20, robotPaint);

        // Cuerpo del robot
        robotPaint.setColor(Color.parseColor("#8E7CC3"));
        tempCanvas.drawRoundRect(new RectF(100, 200, 300, 350), 15, 15, robotPaint);

        // Ojos del robot
        robotPaint.setColor(Color.WHITE);
        tempCanvas.drawCircle(160, 130, 20, robotPaint); // ojo izquierdo
        tempCanvas.drawCircle(240, 130, 20, robotPaint); // ojo derecho

        // Pupilas
        robotPaint.setColor(Color.BLACK);
        tempCanvas.drawCircle(160, 130, 10, robotPaint);
        tempCanvas.drawCircle(240, 130, 10, robotPaint);

        // Sonrisa
        robotPaint.setColor(Color.parseColor("#D4AF37"));
        robotPaint.setStyle(Paint.Style.STROKE);
        robotPaint.setStrokeWidth(6f);
        RectF smileRect = new RectF(170, 140, 230, 180);
        tempCanvas.drawArc(smileRect, 0, 180, false, robotPaint);

        // Brazos
        robotPaint.setColor(Color.parseColor("#4A90E2"));
        robotPaint.setStyle(Paint.Style.FILL);
        tempCanvas.drawRoundRect(new RectF(60, 220, 100, 280), 10, 10, robotPaint); // brazo izquierdo
        tempCanvas.drawRoundRect(new RectF(300, 220, 340, 280), 10, 10, robotPaint); // brazo derecho

        // Piernas
        tempCanvas.drawRoundRect(new RectF(130, 350, 170, 380), 8, 8, robotPaint); // pierna izquierda
        tempCanvas.drawRoundRect(new RectF(230, 350, 270, 380), 8, 8, robotPaint); // pierna derecha

        // Detalles decorativos
        robotPaint.setColor(Color.parseColor("#D4AF37"));
        tempCanvas.drawCircle(200, 250, 8, robotPaint); // botón central
        tempCanvas.drawRect(180, 280, 220, 290, robotPaint); // panel

        // Antenas
        robotPaint.setColor(Color.parseColor("#FF6B6B"));
        tempCanvas.drawRect(195, 60, 205, 80, robotPaint); // antena
        tempCanvas.drawCircle(200, 55, 8, robotPaint); // bolita de antena
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
