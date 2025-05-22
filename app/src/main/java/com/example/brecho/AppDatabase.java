package com.example.brecho;


import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;
import android.content.Context;
import java.util.List;


@Database(entities = {Roupa.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;


    public abstract RoupaDao roupaDao();


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


    @Dao
    public interface RoupaDao {
        @Insert
        void insert(Roupa roupa);


        @Update
        void update(Roupa roupa);


        @Delete
        void delete(Roupa roupa);


        @Query("SELECT * FROM roupas")
        List<Roupa> getAll();
    }
}
