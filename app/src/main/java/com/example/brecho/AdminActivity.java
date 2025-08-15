package com.example.brecho;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.brecho.databinding.ActivityAdminBinding;
import com.example.brecho.databinding.DialogRoupaBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AdminActivity extends AppCompatActivity implements RoupaAdapter.OnRoupaClickListener {

    private ActivityAdminBinding binding;
    private AppDatabase db;
    private RoupaAdapter adapter;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private SessionManager sessionManager;
    private static final String TAG = "AdminActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (binding.adminToolbar != null) {
            setSupportActionBar(binding.adminToolbar);
            binding.adminToolbar.getMenu().clear();
            binding.adminToolbar.inflateMenu(R.menu.menu_admin);
            binding.adminToolbar.setOnMenuItemClickListener(item -> onOptionsItemSelected(item));
        } else {
            setTitle("Painel do Administrador");
        }

        db = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Intent i = new Intent(this, WelcomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }

        setupRecyclerView();
        buscarRoutasSupabase(); // Corrigido para buscar roupas do Supabase na inicialização
        setupButtonClickListeners();
    }

    private void setupRecyclerView() {
        adapter = new RoupaAdapter(new ArrayList<>(), this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    // MÉTODO ATUALIZADO
    private void adicionarRoupaSupabase(String nome, String tamanho, String cor, double preco, String imagemUrl) {
        String url = SupabaseConfig.POSTGREST_BASE + "/roupas";
        String apiKey = SupabaseConfig.SUPABASE_ANON_KEY;

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nome", nome);
            jsonBody.put("tamanho", tamanho);
            jsonBody.put("cor", cor);
            jsonBody.put("preco", preco); // CAMPO NOVO
            if (imagemUrl != null && !imagemUrl.trim().isEmpty()) {
                jsonBody.put("imagem_url", imagemUrl); // CAMPO NOVO
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    Toast.makeText(getApplicationContext(), "Roupa adicionada no Supabase!", Toast.LENGTH_SHORT).show();
                    buscarRoutasSupabase(); // Atualiza a lista após adicionar
                },
                error -> {
                    String msg = "Erro ao adicionar roupa";
                    if (error != null && error.networkResponse != null) {
                        msg += " (Status: " + error.networkResponse.statusCode + ")";
                        Log.e(TAG, "Erro Volley: " + new String(error.networkResponse.data));
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", apiKey);
                headers.put("Authorization", "Bearer " + apiKey);
                headers.put("Content-Type", "application/json");
                headers.put("Prefer", "return=representation");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }

    // MÉTODO ATUALIZADO
    private void buscarRoutasSupabase() {
        String url = "https://hamqyanzgfzcxnxnqzev.supabase.co/rest/v1/roupas?select=*&order=created_at.desc";
        // CORREÇÃO DE SEGURANÇA: Usando a chave ANON, não a SERVICE_ROLE
        String apiKey = SupabaseConfig.SUPABASE_ANON_KEY;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Roupa> lista = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject roupaJson = response.getJSONObject(i);
                            String nome = roupaJson.optString("nome", "");
                            String tamanho = roupaJson.optString("tamanho", "");
                            String cor = roupaJson.optString("cor", "");
                            double preco = roupaJson.optDouble("preco", 0.0); // CAMPO NOVO
                            String imagemUrl = roupaJson.optString("imagem_url", ""); // CAMPO NOVO

                            // Usando o construtor atualizado
                            lista.add(new Roupa(nome, cor, tamanho, preco, imagemUrl));
                        }
                        adapter.atualizarLista(lista);
                        binding.emptyView.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao processar JSON das roupas", e);
                        Toast.makeText(getApplicationContext(), "Erro ao processar roupas", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Erro ao buscar roupas do Supabase", error);
                    Toast.makeText(getApplicationContext(), "Erro ao buscar roupas", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", apiKey);
                headers.put("Authorization", "Bearer " + apiKey);
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }

    private void setupButtonClickListeners() {
        binding.fabAdd.setOnClickListener(v -> mostrarDialogAdicionarOuEditar(null));
    }

    // MÉTODO ATUALIZADO
    private void mostrarDialogAdicionarOuEditar(final Roupa roupaExistente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String dialogTitle = (roupaExistente == null) ? "Adicionar Nova Roupa" : "Editar Roupa";
        builder.setTitle(dialogTitle);

        DialogRoupaBinding dialogBinding = DialogRoupaBinding.inflate(LayoutInflater.from(this));
        builder.setView(dialogBinding.getRoot());

        if (roupaExistente != null) {
            dialogBinding.etNome.setText(roupaExistente.getNome());
            dialogBinding.etCor.setText(roupaExistente.getCor());
            dialogBinding.etTamanho.setText(roupaExistente.getTamanho());
            // Preenche os campos novos ao editar
            dialogBinding.etPreco.setText(String.format(Locale.US, "%.2f", roupaExistente.getPreco()));
            dialogBinding.etImagemUrl.setText(roupaExistente.getImagemUrl());
        }

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String nome = dialogBinding.etNome.getText().toString().trim();
            String cor = dialogBinding.etCor.getText().toString().trim();
            String tamanho = dialogBinding.etTamanho.getText().toString().trim();
            String precoStr = dialogBinding.etPreco.getText().toString().trim();
            String imagemUrl = dialogBinding.etImagemUrl.getText().toString().trim();

            if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(precoStr)) {
                Toast.makeText(this, "Nome e Preço são obrigatórios.", Toast.LENGTH_LONG).show();
                return;
            }

            double preco;
            try {
                preco = Double.parseDouble(precoStr.replace(",", "."));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Formato de preço inválido. Use um número.", Toast.LENGTH_SHORT).show();
                return;
            }

            executor.execute(() -> {
                if (roupaExistente == null) { // Adicionar
                    // Adiciona primeiro no Supabase
                    adicionarRoupaSupabase(nome, tamanho, cor, preco, imagemUrl);

                    // Adiciona no banco de dados local (pode ser removido se Supabase for a única fonte)
                    Roupa novaRoupa = new Roupa(nome, cor, tamanho, preco, imagemUrl);
                    db.roupaDao().insert(novaRoupa);
                } else { // Editar
                    // TODO: Implementar a lógica de EDIÇÃO no Supabase se necessário.
                    // Isso exigiria um método `editarRoupaSupabase` que usa o método HTTP PATCH/PUT.

                    roupaExistente.setNome(nome);
                    roupaExistente.setCor(cor);
                    roupaExistente.setTamanho(tamanho);
                    roupaExistente.setPreco(preco);
                    roupaExistente.setImagemUrl(imagemUrl);
                    db.roupaDao().update(roupaExistente);

                    runOnUiThread(() -> {
                        Toast.makeText(AdminActivity.this, "Roupa atualizada localmente!", Toast.LENGTH_SHORT).show();
                        buscarRoutasSupabase(); // Atualiza a lista para refletir a mudança
                    });
                }
            });
        });

        builder.setNegativeButton("Cancelar", null);

        if (roupaExistente != null) {
            builder.setNeutralButton("Excluir", (dialog, which) -> {
                new AlertDialog.Builder(this)
                        .setTitle("Confirmar Exclusão")
                        .setMessage("Tem certeza que deseja excluir a peça '" + roupaExistente.getNome() + "'?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Sim, Excluir", (confirmDialog, confirmWhich) -> {
                            executor.execute(() -> {
                                // TODO: Implementar a lógica de EXCLUSÃO no Supabase se necessário.
                                // Isso exigiria um método `excluirRoupaSupabase` que usa o método HTTP DELETE.

                                db.roupaDao().delete(roupaExistente);
                                runOnUiThread(() -> {
                                    Toast.makeText(AdminActivity.this, "Roupa excluída localmente.", Toast.LENGTH_SHORT).show();
                                    buscarRoutasSupabase();
                                });
                            });
                        })
                        .setNegativeButton("Não", null)
                        .show();
            });
        }
        builder.create().show();
    }

    @Override
    public void onRoupaClick(Roupa roupa) {
        if (roupa != null) {
            Log.d(TAG, "Item clicado: " + roupa.getNome());
            mostrarDialogAdicionarOuEditar(roupa);
        } else {
            Log.w(TAG, "onRoupaClick chamado com roupa nula.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Sair")
                    .setMessage("Tem certeza que deseja sair?")
                    .setPositiveButton("Sim", (d, w) -> {
                        sessionManager.clear();
                        Intent intent = new Intent(AdminActivity.this, WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Não", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}