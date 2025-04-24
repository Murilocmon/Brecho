package com.example.brecho;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.brecho.RoupaAdapter;
import com.example.brecho.AppDatabase;
import com.example.brecho.databinding.ActivityClienteBinding;
import com.example.brecho.databinding.DialogReservaBinding;
import com.example.brecho.Roupa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.content.Intent;

public class ClienteActivity extends AppCompatActivity implements RoupaAdapter.OnRoupaClickListener {
    private ActivityClienteBinding binding;
    private AppDatabase db;
    private RoupaAdapter adapter;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getDatabase(this);
        adapter = new RoupaAdapter(this, List.of(), this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        carregarRoupas();

        binding.btnFiltrar.setOnClickListener(v -> filtrarRoupas());
        binding.btnSacola.setOnClickListener(v -> {
            startActivity(new Intent(this, SacolaActivity.class));
        });
    }

    private void carregarRoupas() {
        executor.execute(() -> {
            List<Roupa> roupas = db.roupaDao().getAllNaoReservadas();
            runOnUiThread(() -> adapter.atualizarLista(roupas));
        });
    }

    private void filtrarRoupas() {
        String cor = binding.etFiltroCor.getText().toString().trim();
        String tamanho = binding.etFiltroTamanho.getText().toString().trim();

        executor.execute(() -> {
            List<Roupa> filtradas = new ArrayList<>();
            for (Roupa r : filtradas) {
                if ((cor.isEmpty() || r.getCor().equalsIgnoreCase(cor)) &&
                        (tamanho.isEmpty() || r.getTamanho().equalsIgnoreCase(tamanho))) {
                    filtradas.add(r);
                }
            }

            runOnUiThread(() -> adapter.atualizarLista(filtradas));
        });
    }

    @Override
    public void onRoupaClick(Roupa roupa) {
        // Implementar se necessário
    }

    @Override
    public void onReservarClick(Roupa roupa) {
        mostrarDialogReserva(roupa);
    }

    private void mostrarDialogReserva(Roupa roupa) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Reservar " + roupa.getNome());

        DialogReservaBinding dialogBinding = DialogReservaBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            int selectedId = dialogBinding.radioGroupDias.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Selecione um período de reserva", Toast.LENGTH_SHORT).show();
                return;
            }

            int dias;
            if (selectedId == R.id.radio2Dias) dias = 2;
            else if (selectedId == R.id.radio3Dias) dias = 3;
            else {
                dias = 1;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, dias);
            long reservadaAte = calendar.getTimeInMillis();

            roupa.setReservada(true);
            roupa.setReservadaAte(reservadaAte);

            executor.execute(() -> {
                db.roupaDao().update(roupa);
                runOnUiThread(() -> {
                    carregarRoupas();
                    Toast.makeText(this, "Roupa reservada por " + dias + " dia(s)", Toast.LENGTH_SHORT).show();
                });
            });
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}