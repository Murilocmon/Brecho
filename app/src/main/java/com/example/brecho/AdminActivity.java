package com.example.brecho;


import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.brecho.RoupaAdapter;
import com.example.brecho.AppDatabase;
import com.example.brecho.databinding.ActivityAdminBinding;
import com.example.brecho.databinding.DialogRoupaBinding;
import com.example.brecho.Roupa;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AdminActivity extends AppCompatActivity implements RoupaAdapter.OnRoupaClickListener {
   private ActivityAdminBinding binding;
   private AppDatabase db;
   private RoupaAdapter adapter;
   private final Executor executor = Executors.newSingleThreadExecutor();


   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       binding = ActivityAdminBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());


       db = AppDatabase.getDatabase(this);
       adapter = new RoupaAdapter(this, List.of(), this);
       binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
       binding.recyclerView.setAdapter(adapter);


       carregarRoupas();


       binding.btnAdicionar.setOnClickListener(v -> mostrarDialogAdicionar());
   }


   private void carregarRoupas() {
       executor.execute(() -> {
           List<Roupa> roupas = db.roupaDao().getAll();
           runOnUiThread(() -> adapter.atualizarLista(roupas));
       });
   }


   private void mostrarDialogAdicionar() {
       android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
       builder.setTitle("Adicionar Roupa");


       DialogRoupaBinding dialogBinding = DialogRoupaBinding.inflate(getLayoutInflater());
       builder.setView(dialogBinding.getRoot());


       builder.setPositiveButton("Salvar", (dialog, which) -> {
           String nome = dialogBinding.etNome.getText().toString().trim();
           String cor = dialogBinding.etCor.getText().toString().trim();
           String tamanho = dialogBinding.etTamanho.getText().toString().trim();


           if (nome.isEmpty() || cor.isEmpty() || tamanho.isEmpty()) {
               Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
               return;
           }


           executor.execute(() -> {
               db.roupaDao().insert(new Roupa(nome, cor, tamanho));
               runOnUiThread(() -> {
                   carregarRoupas();
                   Toast.makeText(this, "Roupa adicionada", Toast.LENGTH_SHORT).show();
               });
           });
       });


       builder.setNegativeButton("Cancelar", null);
       builder.show();
   }


   @Override
   public void onRoupaClick(Roupa roupa) {
       mostrarDialogEditar(roupa);
   }


   @Override
   public void onReservarClick(Roupa roupa) {
       // Não usado no admin
   }


   private void mostrarDialogEditar(Roupa roupa) {
       android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
       builder.setTitle("Editar Roupa");


       DialogRoupaBinding dialogBinding = DialogRoupaBinding.inflate(getLayoutInflater());
       dialogBinding.etNome.setText(roupa.getNome());
       dialogBinding.etCor.setText(roupa.getCor());
       dialogBinding.etTamanho.setText(roupa.getTamanho());
       builder.setView(dialogBinding.getRoot());


       builder.setPositiveButton("Salvar", (dialog, which) -> {
           roupa.setNome(dialogBinding.etNome.getText().toString().trim());
           roupa.setCor(dialogBinding.etCor.getText().toString().trim());
           roupa.setTamanho(dialogBinding.etTamanho.getText().toString().trim());


           executor.execute(() -> {
               db.roupaDao().update(roupa);
               runOnUiThread(() -> {
                   carregarRoupas();
                   Toast.makeText(this, "Roupa atualizada", Toast.LENGTH_SHORT).show();
               });
           });
       });


       builder.setNegativeButton("Cancelar", null);


       builder.setNeutralButton("Excluir", (dialog, which) -> {
           executor.execute(() -> {
               db.roupaDao().delete(roupa);
               runOnUiThread(() -> {
                   carregarRoupas();
                   Toast.makeText(this, "Roupa excluída", Toast.LENGTH_SHORT).show();
               });
           });
       });


       builder.show();
   }
}
