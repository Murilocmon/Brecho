package com.example.brecho;

import android.app.Application; // Alterado de Context para Application para uso com ViewModel (boa prática)
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future; // Para getAllRoupas se quiser retornar LiveData ou similar

public class RoupaRepository {
    private AppDatabase.RoupaDao roupaDao;
    private ExecutorService executorService;

    // Construtor recebe Application, que é um Context
    public RoupaRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        roupaDao = db.roupaDao();
        executorService = Executors.newFixedThreadPool(4); // Ou SingleThreadExecutor se preferir
    }

    public void insert(Roupa roupa) {
        executorService.execute(() -> roupaDao.insert(roupa));
    }

    public void update(Roupa roupa) {
        executorService.execute(() -> roupaDao.update(roupa));
    }

    public void delete(Roupa roupa) {
        executorService.execute(() -> roupaDao.delete(roupa));
    }

    public void getAllRoupasAsync(OnRoupasLoadedListener listener) {
        executorService.execute(() -> {
            List<Roupa> roupas = roupaDao.getAll();
            if (listener != null) {
                // Supondo que o listener tem um método para rodar na UI thread
                // Ex: listener.onRoupasLoaded(roupas);
            }
        });
    }

    public interface OnRoupasLoadedListener {
        void onRoupasLoaded(List<Roupa> roupas);
    }

    // Método síncrono simples (CUIDADO: não chame da UI Thread diretamente)
    public List<Roupa> getAllRoupasSync() {
        // Esta é uma operação bloqueante. O ExecutorService do repositório é para insert/update/delete.
        // Se quiser usar este método, quem chama precisa garantir que está em background.
        // Geralmente, DAOs são chamados em background e a UI é atualizada via runOnUiThread ou LiveData.
        return roupaDao.getAll();
    }
}