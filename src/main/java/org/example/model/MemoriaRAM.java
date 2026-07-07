package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class MemoriaRAM {
    private final int capacidadeMaxima;
    private final List<Processo> processos = new ArrayList<>();

    public MemoriaRAM(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public List<Processo> getProcessos() {
        return processos;
    }

    public boolean isCheia() {
        return processos.size() >= capacidadeMaxima;
    }

    public int size() {
        return processos.size();
    }

    public boolean isEmpty() {
        return processos.isEmpty();
    }

    public void adicionar(Processo p) {
        processos.add(p);
    }

    public boolean remover(Processo p) {
        return processos.remove(p);
    }

    public int contarPorTipo(TipoCarga tipo) {
        int count = 0;
        for (Processo p : processos) {
            if (p.getTipo() == tipo) {
                count++;
            }
        }
        return count;
    }
}
