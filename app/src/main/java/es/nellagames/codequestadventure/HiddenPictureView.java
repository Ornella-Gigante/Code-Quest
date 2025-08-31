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
    private int totalPieces = 10; // SIEMPRE 10 piezas
    private int currentImageResource;
    private String currentImageName;
    private String currentImageDescription;
    private Random random = new Random();

    public HiddenPictureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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
        revealedPieces = 0; // Asegurar que empiece en 0
    }

    private void selectRandomImage() {
        int idx = random.nextInt(IMAGE_RESOURCES.length);
        currentImageResource = IMAGE_RESOURCES[idx];
        currentImageName = "";
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
        super.onSizeChanged(w, h, oldw, oldh);

        if (w > 0 && h > 0) {
            maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            maskCanvas = new Canvas(maskBitmap);

            createPuzzlePieces(w, h);
            updateMask(); // Esto cubrirá toda la imagen inicialmente
        }
    }

    private void createPuzzlePieces(int w, int h) {
        puzzlePieces.clear();

        // SIEMPRE crear exactamente 10 piezas en una cuadrícula 2x5 o 5x2
        int cols = 5;
        int rows = 2;

        float pieceWidth = (float) w / cols;
        float pieceHeight = (float) h / rows;

        for (int i = 0; i < 10; i++) { // Exactamente 10 piezas
            int row = i / cols;
            int col = i % cols;

            float left = col * pieceWidth;
            float top = row * pieceHeight;
            float right = Math.min((col + 1) * pieceWidth, w);
            float bottom = Math.min((row + 1) * pieceHeight, h);

            puzzlePieces.add(new RectF(left, top, right, bottom));
        }
    }

    public void revealPieces(int count) {
        if (count < 0) count = 0;
        if (count > 10) count = 10; // Máximo 10 piezas

        revealedPieces = count;
        updateMask();
        invalidate();
    }

    public void updateMask() {
        if (maskCanvas == null) return;

        // Cubrir TODA la imagen con la máscara gris
        maskCanvas.drawColor(Color.parseColor("#BDBDBD"), android.graphics.PorterDuff.Mode.SRC);

        // Solo revelar las piezas correspondientes al número de respuestas correctas
        for (int i = 0; i < revealedPieces && i < puzzlePieces.size(); i++) {
            maskCanvas.drawRect(puzzlePieces.get(i), revealPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.parseColor("#E0F2F1"));
        canvas.drawRect(4, 4, getWidth() - 4, getHeight() - 4, borderPaint);

        if (hiddenPicture != null && maskBitmap != null) {
            // Dibujar la imagen completa primero
            RectF dest = new RectF(10, 10, getWidth() - 10, getHeight() - 10);
            canvas.drawBitmap(hiddenPicture, null, dest, null);

            // Aplicar la máscara que oculta las partes no reveladas
            canvas.drawBitmap(maskBitmap, 0, 0, null);

            // OPCIONAL: Dibujar líneas sutiles de la cuadrícula solo en las partes reveladas
            // Comentado para mostrar la imagen sin filtros verdes
            /*
            for (int i = 0; i < revealedPieces && i < puzzlePieces.size(); i++) {
                RectF piece = puzzlePieces.get(i);
                canvas.drawRect(piece, gridPaint);
            }
            */
        }
    }

    public int getTotalPieces() {
        return 10; // SIEMPRE 10 piezas
    }

    public void setTotalPieces(int totalPieces) {
        // Ignorar el parámetro, siempre usar 10
        this.totalPieces = 10;

        // Si el número de piezas reveladas excede 10, ajustarlo
        if (revealedPieces > 10) {
            revealedPieces = 10;
        }

        // Recrear las piezas si ya tenemos dimensiones
        if (getWidth() > 0 && getHeight() > 0) {
            createPuzzlePieces(getWidth(), getHeight());
            updateMask();
            invalidate();
        }
    }

    public int getRevealedPieces() {
        return revealedPieces;
    }

    public boolean isComplete() {
        return revealedPieces >= 10; // Completado cuando se revelan las 10 piezas
    }

    // Método para reiniciar completamente la vista
    public void resetPuzzle() {
        revealedPieces = 0;
        selectRandomImage();
        loadPicture();
        updateMask();
        invalidate();
    }
}