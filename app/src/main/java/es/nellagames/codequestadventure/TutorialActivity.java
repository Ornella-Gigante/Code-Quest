package es.nellagames.codequestadventure;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity extends AppCompatActivity {

    private TextView tutorialText;
    private ImageView tutorialImage;
    private Button nextButton, prevButton;

    private SoundManager soundManager;

    private String[] pagesText = {
            "Welcome to CodeQuest Adventure!\nLet's learn to program step-by-step.",
            "Drag and drop code blocks to complete the programs.\nHave fun solving!",
            "Use hints wisely if stuck.\nTry to solve it by thinking first!",
            "Complete challenges to reveal awesome hidden pictures!",
            "Good luck!\nReady? Let's start coding!"
    };


    private int[] pagesBgColors = {
            Color.parseColor("#FFC107"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#FF5722"),
    };

    private int[] pagesImages = {
            R.drawable.tutorial1_fun,
            R.drawable.tutorial2_fun,
            R.drawable.tutorial3_fun,
            R.drawable.tutorial4_fun,
            R.drawable.tutorial5_fun,
    };

    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tutorialText = findViewById(R.id.tutorialText);
        tutorialImage = findViewById(R.id.tutorialImage);
        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);

        // Inicializar SoundManager y música de fondo
        soundManager = new SoundManager(this);
        soundManager.startBackgroundMusic();

        updatePage();

        nextButton.setOnClickListener(v -> {
            soundManager.playSuccess(); // sonido al pasar página
            if (currentPage < pagesText.length - 1) {
                currentPage++;
                updatePage();
            } else {
                finish(); // Cerrar tutorial en la última página
            }
        });

        prevButton.setOnClickListener(v -> {
            soundManager.playError(); // sonido distinto al retroceder
            if (currentPage > 0) {
                currentPage--;
                updatePage();
            }
        });
    }

    private void updatePage() {
        // Update background color
        findViewById(android.R.id.content).setBackgroundColor(pagesBgColors[currentPage]);

        // Set tutorial text with styling
        tutorialText.setText(pagesText[currentPage]);
        tutorialText.setTextColor(Color.WHITE);
        tutorialText.setTextSize(26f);
        tutorialText.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
        tutorialText.setShadowLayer(8f, 4f, 4f, Color.BLACK);

        // Set image resource
        tutorialImage.setImageResource(pagesImages[currentPage]);

        // Update navigation buttons
        prevButton.setVisibility(currentPage == 0 ? View.GONE : View.VISIBLE);
        nextButton.setText(currentPage == pagesText.length - 1 ? "Finish" : "Next");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (soundManager != null) soundManager.resumeBackgroundMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundManager != null) soundManager.pauseBackgroundMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) soundManager.release();
    }
}
