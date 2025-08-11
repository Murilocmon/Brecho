package com.example.brecho;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brecho.databinding.ItemRoupaBinding;
import java.util.ArrayList;
import java.util.List;

public class RoupaAdapter extends RecyclerView.Adapter<RoupaAdapter.RoupaViewHolder> {
    private List<Roupa> roupas;
    private final OnRoupaClickListener listenerExterno; // Renomeado para clareza

    public interface OnRoupaClickListener {
        void onRoupaClick(Roupa roupa);
    }

    public RoupaAdapter(List<Roupa> roupasIniciais, OnRoupaClickListener listener) {
        this.roupas = new ArrayList<>(roupasIniciais != null ? roupasIniciais : new ArrayList<>());
        this.listenerExterno = listener;
    }

    public void atualizarLista(List<Roupa> novasRoupas) {
        this.roupas.clear();
        if (novasRoupas != null) {
            this.roupas.addAll(novasRoupas);
        }
        notifyDataSetChanged(); // Para uma lista grande, considere usar DiffUtil
    }

    @NonNull
    @Override
    public RoupaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRoupaBinding binding = ItemRoupaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        // Passa o listenerExterno para o construtor do ViewHolder
        return new RoupaViewHolder(binding, listenerExterno);
    }

    @Override
    public void onBindViewHolder(@NonNull RoupaViewHolder holder, int position) {
        Roupa roupaAtual = roupas.get(position);
        holder.bind(roupaAtual);
    }

    @Override
    public int getItemCount() {
        return roupas == null ? 0 : roupas.size();
    }

    // RoupaViewHolder é uma classe estática aninhada
    static class RoupaViewHolder extends RecyclerView.ViewHolder {
        private final ItemRoupaBinding binding;
        private final OnRoupaClickListener onItemClickListener; // Listener armazenado no ViewHolder

        public RoupaViewHolder(ItemRoupaBinding binding, final OnRoupaClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.onItemClickListener = listener; // Armazena o listener recebido
        }

        public void bind(final Roupa roupa) {
            binding.textNome.setText(roupa.getNome());
            String cor = roupa.getCor();
            String tamanho = roupa.getTamanho();

            binding.textCor.setText("Cor: " + (cor != null && !cor.isEmpty() ? cor : "N/D"));
            binding.textTamanho.setText("Tamanho: " + (tamanho != null && !tamanho.isEmpty() ? tamanho : "N/D"));

            binding.btnReservar.setVisibility(View.GONE); // Conforme seu código original

            // Configura o OnClickListener para o itemView (o card inteiro)
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) { // Usa o listener armazenado no ViewHolder
                    onItemClickListener.onRoupaClick(roupa);
                }
            });
        }
    }
}