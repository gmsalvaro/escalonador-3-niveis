package org.example.model;

import java.util.LinkedList;
import java.util.Queue;

public class FilaDeProntos {
    private final Queue<Processo> fila = new LinkedList<>();

    public void adicionar(Processo p) {
        fila.offer(p);
    }

    public Processo desenfileirar() {
        return fila.poll();
    }

    public boolean remover(Processo p) {
        return fila.remove(p);
    }

    public boolean isEmpty() {
        return fila.isEmpty();
    }

    public int size() {
        return fila.size();
    }

    public Queue<Processo> getFila() {
        return fila;
    }
}
