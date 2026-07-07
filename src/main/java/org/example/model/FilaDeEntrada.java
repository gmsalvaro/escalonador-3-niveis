package org.example.model;

import java.util.LinkedList;
import java.util.Queue;

public class FilaDeEntrada {
    private final Queue<Tarefa> fila = new LinkedList<>();

    public void adicionar(Tarefa tarefa) {
        fila.offer(tarefa);
    }

    public boolean isEmpty() {
        return fila.isEmpty();
    }

    public int size() {
        return fila.size();
    }

    public Tarefa peek() {
        return fila.peek();
    }

    public Tarefa poll() {
        return fila.poll();
    }

    public boolean remove(Tarefa tarefa) {
        return fila.remove(tarefa);
    }

    public Queue<Tarefa> getFila() {
        return fila;
    }
}
