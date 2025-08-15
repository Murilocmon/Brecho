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
   private double preco; // CAMPO ADICIONADO
   private String imagemUrl; // CAMPO ADICIONADO

   // Construtor atualizado para incluir os novos campos
   public Roupa(String nome, String cor, String tamanho, double preco, String imagemUrl) {
      this.nome = nome;
      this.cor = cor;
      this.tamanho = tamanho;
      this.preco = preco;
      this.imagemUrl = imagemUrl;
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

   public double getPreco() { return preco; } // GETTER ADICIONADO
   public void setPreco(double preco) { this.preco = preco; } // SETTER ADICIONADO

   public String getImagemUrl() { return imagemUrl; } // GETTER ADICIONADO
   public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; } // SETTER ADICIONADO
}