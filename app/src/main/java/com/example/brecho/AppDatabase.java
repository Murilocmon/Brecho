package com.example.brecho;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import android.content.Context;
import java.util.List;

@Database(entities = {Roupa.class, AdminUser.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    // DAO para Roupa (como no seu original)
    @Dao
    public interface RoupaDao {
        @Insert
        void insert(Roupa roupa);

        @Update
        void update(Roupa roupa);

        @Delete
        void delete(Roupa roupa);

        @Query("SELECT * FROM roupas ORDER BY id DESC") // Ordenar para ver os mais recentes primeiro
        List<Roupa> getAll();

        @Query("SELECT * FROM roupas WHERE id = :roupaId")
        Roupa getRoupaById(int roupaId); // Método para buscar por ID, se necessário
    }

    public abstract RoupaDao roupaDao(); // Método de acesso para RoupaDao
    public abstract AdminUserDao adminUserDao(); // Método de acesso para AdminUserDao

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "brecho_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}