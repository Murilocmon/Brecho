package com.example.brecho;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.brecho.databinding.ActivityLoginBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AppDatabase db;
    private SessionManager sessionManager;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        // checkAdminExistence é chamado no onResume para sempre refletir o estado atual
        // ao entrar na tela.

        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        binding.btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterAdminActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAdminExistence();
        if (binding.etLoginUsername.getText() != null) {
            binding.etLoginUsername.getText().clear();
        }
        if (binding.etLoginPassword.getText() != null) {
            binding.etLoginPassword.getText().clear();
        }
        binding.tilLoginUsername.setError(null);
        binding.tilLoginPassword.setError(null);
        binding.etLoginUsername.requestFocus();
    }

    private void checkAdminExistence() {
        // Sem dependência do Room para o fluxo do Supabase.
        binding.btnGoToRegister.setVisibility(View.VISIBLE);
        binding.txtLoginTitle.setText("Login de Administrador");
        binding.btnLogin.setText("Entrar");
        binding.tilLoginUsername.setVisibility(View.VISIBLE);
        binding.tilLoginPassword.setVisibility(View.VISIBLE);
    }

    private void attemptLogin() {
        executor.execute(() -> {
            runOnUiThread(() -> binding.progress.setVisibility(View.VISIBLE));
            String username = binding.etLoginUsername.getText().toString().trim();
            String password = binding.etLoginPassword.getText().toString().trim();

            runOnUiThread(() -> {
                binding.tilLoginUsername.setError(null);
                binding.tilLoginPassword.setError(null);
            });

            if (TextUtils.isEmpty(username)) {
                runOnUiThread(() -> binding.tilLoginUsername.setError("Usuário é obrigatório"));
                return;
            }

            if (TextUtils.isEmpty(password)) {
                runOnUiThread(() -> binding.tilLoginPassword.setError("Senha é obrigatória"));
                return;
            }

            try {
                URL url = new URL(SupabaseConfig.POSTGREST_BASE + "/admin_users?username=eq." + username);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", SupabaseConfig.SUPABASE_ANON_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.SUPABASE_ANON_KEY);
                conn.setRequestProperty("Content-Type", "application/json");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONArray arr = new JSONArray(response.toString());
                if (arr.length() > 0) {
                    JSONObject user = arr.getJSONObject(0);
                    String hash = user.getString("password_hash");
                    if (PasswordHasher.verifyPassword(password, hash)) {
                        // Não temos JWT do GoTrue aqui; salvamos sessão simples
                        sessionManager.saveSession(username, "local");
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Usuário ou senha incorretos", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
            runOnUiThread(() -> binding.progress.setVisibility(View.GONE));
        });
    }
}