package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class ArmazenamentoSwap {
    private final List<Processo> processos = new ArrayList<>();

    public void adicionar(Processo p) {
        processos.add(p);
    }

    public boolean remover(Processo p) {
        return processos.remove(p);
    }

    public boolean isEmpty() {
        return processos.isEmpty();
    }

    public List<Processo> getProcessos() {
        return processos;
    }

    public int size() {
        return processos.size();
    }

    public void envelhecerProcessos(int delta) {
        for (Processo p : processos) {
            p.envelhecerDisco(delta);
        }
    }
}
