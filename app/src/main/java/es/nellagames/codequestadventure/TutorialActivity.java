package es.nellagames.codequestadventure;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import es.nellagames.codequestadventure.R;

public class TutorialActivity extends AppCompatActivity {

    private TextView tutorialText;
    private ImageView tutorialImage;
    private Button nextButton, prevButton;

    private String[] pagesText = {
            "Welcome to CodeQuest Adventure! Let's learn to program step-by-step.",
            "Drag and drop code blocks to complete the programs. Have fun solving!",
            "Use hints wisely if stuck. Try to solve it by thinking first!",
            "Complete challenges to reveal awesome hidden pictures!",
            "Good luck! Ready? Let's start coding!"
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

        updatePage();

        nextButton.setOnClickListener(v -> {
            if (currentPage < pagesText.length - 1) {
                currentPage++;
                updatePage();
            } else {
                finish(); // Close tutorial on last page
            }
        });

        prevButton.setOnClickListener(v -> {
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
}
