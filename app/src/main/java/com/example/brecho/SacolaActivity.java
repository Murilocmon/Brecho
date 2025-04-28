package com.example.brecho;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.brecho.RoupaAdapter;
import com.example.brecho.AppDatabase;
import com.example.brecho.databinding.ActivitySacolaBinding;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class SacolaActivity extends AppCompatActivity {
   private ActivitySacolaBinding binding;
   private AppDatabase db;
   private RoupaAdapter adapter;
   private final Executor executor = Executors.newSingleThreadExecutor();


   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       binding = ActivitySacolaBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());


       db = AppDatabase.getDatabase(this);
       adapter = new RoupaAdapter(this, List.of(), null);
       binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
       binding.recyclerView.setAdapter(adapter);


       carregarRoupasReservadas();


       binding.btnVoltar.setOnClickListener(v -> finish());
   }


   private void carregarRoupasReservadas() {
       executor.execute(() -> {
           List<Roupa> reservadas = db.roupaDao().getAllReservadas();
           runOnUiThread(() -> adapter.atualizarLista(reservadas));
       });
   }
}
