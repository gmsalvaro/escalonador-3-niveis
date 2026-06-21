package org.example.model;

public class Processo {
    int id;
    Estado estado;
    int tempoTotalNecessario;
    int tempoExecutado = 0;
    int tempoNoDisco = 0;
    int tempoNaMemoria = 0;

    public Processo(int id, int tempoTotalNecessario) {
        this.id = id;
        this.tempoTotalNecessario = tempoTotalNecessario;
        this.estado = Estado.PRONTO;
    }

    // Metodo para envelhecer o processo no disco
    public void envelhecerDisco(int quantum) {
        tempoNoDisco += quantum;
    }

    public void envelhecerMemoria(int quantum) {
        tempoNaMemoria += quantum;
    }

    public int getTempoNaMemoria() {
        return tempoNaMemoria;
    }

    public void setTempoNaMemoria(int tempoNaMemoria) {
        this.tempoNaMemoria = tempoNaMemoria;
    }

    public void tempoExecutado(int quantum) {
        tempoExecutado += quantum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public int getTempoNoDisco() {
        return tempoNoDisco;
    }

    public void setTempoNoDisco(int tempoNoDisco) {
        this.tempoNoDisco = tempoNoDisco;
    }

    public boolean isConcluido() {
        return tempoExecutado >= tempoTotalNecessario;
    }

    @Override
    public String toString() {
        return "P" + id + "(" + Math.min(tempoExecutado, tempoTotalNecessario) + "/" + tempoTotalNecessario + ")";
    }

}
