package com.example.brecho;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.brecho.databinding.ActivityRegisterAdminBinding;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterAdminActivity extends AppCompatActivity {

    private ActivityRegisterAdminBinding binding;
    private AppDatabase db;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private static final String TAG = "RegisterAdminActivity"; // Para logs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Cadastro de Administrador");
        db = AppDatabase.getDatabase(this);

        binding.btnRegister.setOnClickListener(v -> registerAdmin());
    }

    private void registerAdmin() {
        String username = binding.etRegisterUsername.getText().toString().trim();
        String password = binding.etRegisterPassword.getText().toString().trim();
        String confirmPassword = binding.etRegisterConfirmPassword.getText().toString().trim();

        binding.tilRegisterUsername.setError(null);
        binding.tilRegisterPassword.setError(null);
        binding.tilRegisterConfirmPassword.setError(null);

        boolean isValid = true;
        if (TextUtils.isEmpty(username)) {
            binding.tilRegisterUsername.setError("Usuário é obrigatório");
            isValid = false;
        }
        if (TextUtils.isEmpty(password)) {
            binding.tilRegisterPassword.setError("Senha é obrigatória");
            isValid = false;
        } else if (password.length() < 6) {
            binding.tilRegisterPassword.setError("Senha deve ter no mínimo 6 caracteres");
            isValid = false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.tilRegisterConfirmPassword.setError("Confirmação de senha é obrigatória");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            binding.tilRegisterConfirmPassword.setError("As senhas não coincidem");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        executor.execute(() -> {
            runOnUiThread(() -> binding.progress.setVisibility(View.VISIBLE));
            try {
                String passwordHash = PasswordHasher.hashPassword(password);
                URL url = new URL(SupabaseConfig.POSTGREST_BASE + "/admin_users");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("apikey", SupabaseConfig.SUPABASE_ANON_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.SUPABASE_ANON_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Prefer", "return=representation");
                conn.setDoOutput(true);

                String jsonInputString = "{\"username\":\"" + username + "\",\"password_hash\":\"" + passwordHash + "\"}";
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();
                if (code == 201 || code == 200) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Administrador cadastrado!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterAdminActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Erro ao cadastrar admin (" + code + ")", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
            runOnUiThread(() -> binding.progress.setVisibility(View.GONE));
        });
    }
}