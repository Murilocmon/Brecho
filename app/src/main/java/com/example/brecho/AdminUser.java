package com.example.brecho;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "admin_users", indices = {@Index(value = {"username"}, unique = true)})
public class AdminUser {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String username;

    @NonNull
    private String passwordHash;

    public AdminUser(@NonNull String username, @NonNull String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(@NonNull String passwordHash) {
        this.passwordHash = passwordHash;
    }
}