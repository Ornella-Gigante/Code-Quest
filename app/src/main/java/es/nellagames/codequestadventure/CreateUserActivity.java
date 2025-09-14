package es.nellagames.codequestadventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateUserActivity extends AppCompatActivity {
    private EditText etNewUsername;
    private Button btnCreate, btnAvatarUnicorn, btnAvatarFox;
    private ImageView avatarPreview;
    private SharedPreferences prefs;
    private Bitmap selectedAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);

        // Usar los IDs correctos que están en tu layout XML
        etNewUsername = findViewById(R.id.etNewUsername);
        btnCreate = findViewById(R.id.btnCreate);
        btnAvatarUnicorn = findViewById(R.id.btnAvatarUnicorn);
        btnAvatarFox = findViewById(R.id.btnAvatarFox);
        avatarPreview = findViewById(R.id.avatarPreview);

        // Avatar por defecto - robot (avatar1) redimensionado
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar1);
        selectedAvatar = resizeBitmap(originalBitmap, 200, 200);
        avatarPreview.setImageBitmap(selectedAvatar);

        // Listener para seleccionar robot (btnAvatarUnicorn en el layout)
        btnAvatarUnicorn.setOnClickListener(v -> {
            Bitmap robotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar1);
            selectedAvatar = resizeBitmap(robotBitmap, 200, 200);
            avatarPreview.setImageBitmap(selectedAvatar);
            Toast.makeText(this, "Robot selected! 🤖", Toast.LENGTH_SHORT).show();
        });

        // Listener para seleccionar gato (btnAvatarFox en el layout)
        btnAvatarFox.setOnClickListener(v -> {
            Bitmap catBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar2);
            selectedAvatar = resizeBitmap(catBitmap, 200, 200);
            avatarPreview.setImageBitmap(selectedAvatar);
            Toast.makeText(this, "Cat selected! 🐱", Toast.LENGTH_SHORT).show();
        });

        // Listener para crear usuario
        btnCreate.setOnClickListener(v -> {
            String username = etNewUsername.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.length() < 3) {
                Toast.makeText(this, "Username must be at least 3 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            LeaderboardDbHelper db = new LeaderboardDbHelper(this);

            try {
                // Verificar si el usuario ya existe
                long existingUserId = db.getUserId(username);
                if (existingUserId != -1) {
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crear nuevo usuario con avatar Bitmap
                long userId = db.insertUserWithImage(username, selectedAvatar);

                if (userId != -1) {
                    // Guardar en SharedPreferences
                    prefs.edit()
                            .putLong("current_user_id", userId)
                            .putString("current_username", username)
                            .putString("current_avatar", "")
                            .apply();

                    Toast.makeText(this, "User created successfully!", Toast.LENGTH_SHORT).show();

                    // Navegación correcta al MainActivity
                    Intent intent = new Intent(CreateUserActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Error creating user", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        });
    }

    // Método para redimensionar bitmap y evitar distorsión
    private Bitmap resizeBitmap(Bitmap original, int newWidth, int newHeight) {
        if (original == null) return null;

        // Usar un tamaño más pequeño para evitar el error "Row too big"
        int size = Math.min(newWidth, newHeight);
        // Reducir a 100x100 para evitar problemas con CursorWindow
        size = Math.min(size, 100);

        Bitmap resized = Bitmap.createScaledBitmap(original, size, size, true);

        // Comprimir aún más si es necesario
        return compressBitmap(resized);
    }

    // Método adicional para comprimir el bitmap
    private Bitmap compressBitmap(Bitmap original) {
        if (original == null) return null;

        try {
            java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
            // Usar compresión JPEG con calidad 70% para reducir tamaño
            original.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] byteArray = stream.toByteArray();

            // Convertir de vuelta a Bitmap
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } catch (Exception e) {
            return original; // Devolver original si falla la compresión
        }
    }

    @Override
    public void onBackPressed() {
        // Regresar al LoginActivity al presionar back
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}