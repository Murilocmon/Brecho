package com.example.brecho;

import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoupaRepository {
    private AppDatabase.RoupaDao roupaDao;
    private ExecutorService executorService;

    public RoupaRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        roupaDao = db.roupaDao();
        executorService = Executors.newFixedThreadPool(4);
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

    public List<Roupa> getAllRoupas() {
        return roupaDao.getAll();
    }

    public List<Roupa> getRoupasNaoReservadas() {
        return roupaDao.getAllNaoReservadas();
    }

    public List<Roupa> getRoupasReservadas() {
        return roupaDao.getAllNaoReservadas();
    }
}
