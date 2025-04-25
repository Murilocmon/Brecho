package com.example.brecho;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.brecho.databinding.ActivityCadastroBinding;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class CadastroActivity extends AppCompatActivity {

    private ActivityCadastroBinding binding;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnCadastrar.setOnClickListener(v -> cadastrarUsuario());
    }

    private void cadastrarUsuario() {
        String email = binding.etEmail.getText().toString().trim();
        String senha = binding.etSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mostrarLoading(true);

        // Criar o JSON para enviar à API
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("senha", senha);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configurar a requisição
        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://seudominio.epizy.com/api/cadastro.php") // SUBSTITUA pelo seu URL
                .post(body)
                .build();

        // Enviar a requisição
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    mostrarLoading(false);
                    Toast.makeText(CadastroActivity.this,
                            "Erro de conexão: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String resposta = response.body().string();
                runOnUiThread(() -> {
                    mostrarLoading(false);
                    try {
                        if (resposta.contains("sucesso")) {
                            Toast.makeText(CadastroActivity.this,
                                    "Cadastro realizado!", Toast.LENGTH_SHORT).show();
                            finish(); // Volta para a tela de login
                        } else {
                            Toast.makeText(CadastroActivity.this,
                                    "Erro: " + resposta, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(CadastroActivity.this,
                                "Erro ao processar resposta", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void mostrarLoading(boolean mostrar) {
        binding.progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        binding.btnCadastrar.setEnabled(!mostrar);
    }
}