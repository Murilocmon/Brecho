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
   // Adicione um campo para o preço se desejar, por exemplo:
   // private double preco;
   // Adicione campos para imagens se desejar, por exemplo:
   // private String imagePath;


   // Construtor usado ao criar uma nova roupa (sem ID, pois é autogerado)
   public Roupa(String nome, String cor, String tamanho) {
      this.nome = nome;
      this.cor = cor;
      this.tamanho = tamanho;
   }

   // Getters e Setters
   public int getId() { return id; }
   public void setId(int id) { this.id = id; } // Room precisa do setter do id

   public String getNome() { return nome; }
   public void setNome(String nome) { this.nome = nome; }

   public String getCor() { return cor; }
   public void setCor(String cor) { this.cor = cor; }

   public String getTamanho() { return tamanho; }
   public void setTamanho(String tamanho) { this.tamanho = tamanho; }

}