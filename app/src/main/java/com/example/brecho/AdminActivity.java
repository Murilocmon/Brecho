package com.example.brecho;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color; // Para o código de depuração (se for usar)
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // Para logs e código de depuração
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View; // Para View.VISIBLE, etc. (se for usar no código de depuração)
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.brecho.databinding.ActivityAdminBinding; // Importa o ViewBinding gerado
import com.example.brecho.databinding.DialogRoupaBinding;   // Importa o ViewBinding para o diálogo
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale; // Para formatar preço, se necessário
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdminActivity extends AppCompatActivity implements RoupaAdapter.OnRoupaClickListener {

    private ActivityAdminBinding binding; // Variável para o ViewBinding da Activity
    private AppDatabase db;
    private RoupaAdapter adapter;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private SessionManager sessionManager;
    private static final String TAG = "AdminActivity"; // Tag para Logs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Infla o layout usando ViewBinding
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        // Define o root do binding como o content view
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
        buscarRoupasSupabase();
        setupButtonClickListeners();
    }

    private void setupRecyclerView() {
        adapter = new RoupaAdapter(new ArrayList<>(), this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }
    private void adicionarRoupaSupabase(String nome, String tamanho, String cor) {
        String url = SupabaseConfig.POSTGREST_BASE + "/roupas";
        String apiKey = SupabaseConfig.SUPABASE_ANON_KEY;

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nome", nome);
            jsonBody.put("tamanho", tamanho);
            jsonBody.put("cor", cor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    Toast.makeText(getApplicationContext(), "Roupa adicionada no Supabase!", Toast.LENGTH_SHORT).show();
                    buscarRoupasSupabase();
                },
                error -> {
                    String msg = "Erro ao adicionar roupa";
                    if (error != null && error.networkResponse != null) {
                        msg += " (" + error.networkResponse.statusCode + ")";
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
    private void buscarRoupasSupabase() {
        String url = "https://hamqyanzgfzcxnxnqzev.supabase.co/rest/v1/roupas?select=*&order=created_at.desc";
        String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhhbXF5YW56Z2Z6Y3hueG5xemV2Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NzgyODY0MCwiZXhwIjoyMDYzNDA0NjQwfQ.H7p2u6K0g2nk7qdYyZQ5EUWC-3Cm8kddbJuGS93ha9Q";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Roupa> lista = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject roupa = response.getJSONObject(i);
                            String nome = roupa.optString("nome", "");
                            String tamanho = roupa.optString("tamanho", "");
                            String cor = roupa.optString("cor", "");
                            lista.add(new Roupa(nome, cor, tamanho));
                        }
                        adapter.atualizarLista(lista);
                        binding.emptyView.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Erro ao processar roupas", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
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

    private void carregarRoupas() {
        Log.d(TAG, "Iniciando carregamento de roupas...");
        executor.execute(() -> {
            try {
                final List<Roupa> roupas = db.roupaDao().getAll();
                Log.d(TAG, "Roupas carregadas do DB: " + (roupas != null ? roupas.size() : "null"));
                runOnUiThread(() -> {
                    if (adapter != null) {
                        adapter.atualizarLista(roupas);
                        Log.d(TAG, "Adapter atualizado na UI thread.");
                    } else {
                        Log.e(TAG, "Adapter é nulo ao tentar atualizar a lista na UI thread.");
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Erro ao carregar roupas do banco de dados.", e);
                runOnUiThread(() -> Toast.makeText(AdminActivity.this, "Erro ao buscar roupas.", Toast.LENGTH_SHORT).show());
            }
        });
    }

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
            // Se você tivesse um campo de preço (exemplo):
            // dialogBinding.etPreco.setText(String.format(Locale.getDefault(), "%.2f", roupaExistente.getPreco()));
        }

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String nome = dialogBinding.etNome.getText().toString().trim();
            String cor = dialogBinding.etCor.getText().toString().trim();
            String tamanho = dialogBinding.etTamanho.getText().toString().trim();
            // String precoStr = dialogBinding.etPreco.getText().toString().trim(); // Exemplo

            if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(cor) || TextUtils.isEmpty(tamanho)) {
                Toast.makeText(this, "Nome, Cor e Tamanho são obrigatórios.", Toast.LENGTH_LONG).show();
                return;
            }

            executor.execute(() -> {
                String feedbackMessage;
                if (roupaExistente == null) { // Adicionar
                    adicionarRoupaSupabase(nome, tamanho, cor);
                }
                if (roupaExistente == null) { // Adicionar
                    Roupa novaRoupa = new Roupa(nome, cor, tamanho /*, preco */);
                    db.roupaDao().insert(novaRoupa);
                    feedbackMessage = "Roupa adicionada com sucesso!";


                } else { // Editar
                    roupaExistente.setNome(nome);
                    roupaExistente.setCor(cor);
                    roupaExistente.setTamanho(tamanho);
                    // roupaExistente.setPreco(preco);
                    db.roupaDao().update(roupaExistente);
                    feedbackMessage = "Roupa atualizada com sucesso!";
                }
                runOnUiThread(() -> {
                    Toast.makeText(AdminActivity.this, feedbackMessage, Toast.LENGTH_SHORT).show();
                    buscarRoupasSupabase();
                });
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
                                db.roupaDao().delete(roupaExistente);
                                runOnUiThread(() -> {
                                    Toast.makeText(AdminActivity.this, "Roupa excluída.", Toast.LENGTH_SHORT).show();
                                    buscarRoupasSupabase();
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