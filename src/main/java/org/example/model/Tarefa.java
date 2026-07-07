package org.example.model;

public class Tarefa {
    private final int id;
    private final TipoCarga tipo;
    private final int tempoTotalNecessario;

    public Tarefa(int id, TipoCarga tipo, int tempoTotalNecessario) {
        this.id = id;
        this.tipo = tipo;
        this.tempoTotalNecessario = tempoTotalNecessario;
    }

    public int getId() {
        return id;
    }

    public TipoCarga getTipo() {
        return tipo;
    }

    public int getTempoTotalNecessario() {
        return tempoTotalNecessario;
    }

    @Override
    public String toString() {
        String t = (tipo == TipoCarga.LIMITADO_CPU) ? "CPU" : "E/S";
        return String.format("Tarefa %d[%s](%d)", id, t, tempoTotalNecessario);
    }
}
