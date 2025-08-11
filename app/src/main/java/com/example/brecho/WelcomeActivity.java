package com.example.brecho;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.brecho.databinding.ActivityWelcomeBinding;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding binding;
    private SessionManager sessionManager;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        checkAdminExistence();

        binding.btnGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        binding.btnGoToRegisterAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterAdminActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAdminExistence();
    }

    private void checkAdminExistence() {
        Log.d(TAG, "Verificando administradores no Supabase...");
        executor.execute(() -> {
            try {
                // 1) Se não houver nenhum admin no Supabase, forçar fluxo de cadastro
                URL url = new URL(SupabaseConfig.POSTGREST_BASE + "/admin_users?select=username&limit=1");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", SupabaseConfig.SUPABASE_ANON_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.SUPABASE_ANON_KEY);
                conn.setRequestProperty("Content-Type", "application/json");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) response.append(line);
                in.close();

                boolean hasAdmin = false;
                try {
                    JSONArray arr = new JSONArray(response.toString());
                    hasAdmin = arr.length() > 0;
                } catch (Exception ignore) { }

                final boolean finalHasAdmin = hasAdmin;
                runOnUiThread(() -> {
                    binding.btnGoToRegisterAdmin.setVisibility(View.VISIBLE);

                    if (!finalHasAdmin) {
                        // Se não há admin, sempre ir para Cadastro e limpar qualquer sessão antiga
                        sessionManager.clear();
                        binding.btnGoToLogin.setVisibility(View.GONE);
                        binding.btnGoToRegisterAdmin.setText("Cadastrar Primeiro Administrador");
                        Intent intent = new Intent(WelcomeActivity.this, RegisterAdminActivity.class);
                        startActivity(intent);
                    } else if (sessionManager.isLoggedIn()) {
                        // Se há admin e já está logado, ir para Admin
                        Intent intent = new Intent(WelcomeActivity.this, AdminActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Há admin, mas não logado: mostrar Login
                        binding.btnGoToLogin.setVisibility(View.VISIBLE);
                        binding.btnGoToRegisterAdmin.setText("Redefinir/Cadastrar Administrador");
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Erro ao consultar Supabase.", e);
                runOnUiThread(() -> {
                    Toast.makeText(WelcomeActivity.this, "Erro ao verificar admin.", Toast.LENGTH_SHORT).show();
                    binding.btnGoToLogin.setVisibility(View.VISIBLE);
                    binding.btnGoToRegisterAdmin.setVisibility(View.VISIBLE);
                    binding.btnGoToRegisterAdmin.setText("Cadastrar Administrador");
                });
            }
        });
    }
}