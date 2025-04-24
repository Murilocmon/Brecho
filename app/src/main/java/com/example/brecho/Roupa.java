package com.example.brecho;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "roupas")
public class Roupa {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nome;
    private String cor;
    private String tamanho;
    private boolean reservada;
    private long reservadaAte;

    public Roupa(String nome, String cor, String tamanho) {
        this.nome = nome;
        this.cor = cor;
        this.tamanho = tamanho;
        this.reservada = false;
        this.reservadaAte = 0;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }
    public boolean isReservada() { return reservada; }
    public void setReservada(boolean reservada) { this.reservada = reservada; }
    public long getReservadaAte() { return reservadaAte; }
    public void setReservadaAte(long reservadaAte) { this.reservadaAte = reservadaAte; }
}