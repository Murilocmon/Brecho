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


   public Roupa(String nome, String cor, String tamanho) {
      this.nome = nome;
      this.cor = cor;
      this.tamanho = tamanho;
   }


   public int getId() { return id; }
   public void setId(int id) { this.id = id; }
   public String getNome() { return nome; }
   public void setNome(String nome) { this.nome = nome; }
   public String getCor() { return cor; }
   public void setCor(String cor) { this.cor = cor; }
   public String getTamanho() { return tamanho; }
   public void setTamanho(String tamanho) { this.tamanho = tamanho; }
}