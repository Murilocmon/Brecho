package com.example.brecho;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brecho.databinding.ItemRoupaBinding;
import java.util.List;


public class RoupaAdapter extends RecyclerView.Adapter<RoupaAdapter.RoupaViewHolder> {
    private List<Roupa> roupas;
    private final Context context;
    private final OnRoupaClickListener listener;


    public interface OnRoupaClickListener {
        void onRoupaClick(Roupa roupa);
    }


    public RoupaAdapter(Context context, List<Roupa> roupas, OnRoupaClickListener listener) {
        this.context = context;
        this.roupas = roupas;
        this.listener = listener;
    }


    public void atualizarLista(List<Roupa> novasRoupas) {
        this.roupas = novasRoupas;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RoupaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRoupaBinding binding = ItemRoupaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new RoupaViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull RoupaViewHolder holder, int position) {
        holder.bind(roupas.get(position));
    }


    @Override
    public int getItemCount() {
        return roupas.size();
    }


    class RoupaViewHolder extends RecyclerView.ViewHolder {
        private final ItemRoupaBinding binding;


        public RoupaViewHolder(ItemRoupaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        public void bind(Roupa roupa) {
            binding.textNome.setText(roupa.getNome());
            binding.textCor.setText("Cor: " + roupa.getCor());
            binding.textTamanho.setText("Tamanho: " + roupa.getTamanho());


            binding.getRoot().setOnClickListener(v -> listener.onRoupaClick(roupa));


            binding.btnReservar.setVisibility(View.GONE); // Esconde botão de reservar
        }
    }
}
