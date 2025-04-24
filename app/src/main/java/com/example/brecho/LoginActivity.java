package com.example.brecho;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.brecho.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnCliente.setOnClickListener(v -> {
            startActivity(new Intent(this, ClienteActivity.class));
        });

        binding.btnAdmin.setOnClickListener(v -> {
            showAdminDialog();
        });
    }

    private void showAdminDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Acesso Administrativo");

        com.example.brecho.databinding.DialogAdminBinding dialogBinding =
                com.example.brecho.databinding.DialogAdminBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        builder.setPositiveButton("Entrar", (dialog, which) -> {
            if (dialogBinding.etSenha.getText().toString().equals("1907")) {
                startActivity(new Intent(this, AdminActivity.class));
            } else {
                android.widget.Toast.makeText(this, "Senha incorreta", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}