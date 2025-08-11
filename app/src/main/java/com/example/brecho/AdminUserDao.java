package com.example.brecho;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface AdminUserDao {

    @Insert
    void insert(AdminUser adminUser);

    @Query("SELECT * FROM admin_users WHERE username = :username LIMIT 1")
    AdminUser findByUsername(String username);

    @Query("SELECT COUNT(*) FROM admin_users")
    int countAdmins();

    @Query("SELECT * FROM admin_users")
    List<AdminUser> getAllAdmins();

    @Query("DELETE FROM admin_users") // <<< NOVO MÉTODO ADICIONADO
    void deleteAllAdmins();
}