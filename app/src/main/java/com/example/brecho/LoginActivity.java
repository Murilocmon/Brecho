package com.example.brecho;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.brecho.databinding.ActivityLoginBinding;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.btnLogin.setOnClickListener(v -> fazerLogin());

        binding.btnCadastrar.setOnClickListener(v -> {
            startActivity(new Intent(this, CadastroActivity.class));
        });
    }

    private void fazerLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String senha = binding.etSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Criar o JSON com os dados
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("senha", senha);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Configurar a requisição
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://seudominio.epizy.com/api/login.php")
                .post(body)
                .build();

        // 3. Enviar a requisição
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resposta = response.body().string();
                runOnUiThread(() -> {
                    try {
                        if (resposta.contains("sucesso")) {
                            // Login bem-sucedido
                            startActivity(new Intent(LoginActivity.this, ClienteActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}