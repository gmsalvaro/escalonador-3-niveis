package org.example.model;

public class Processo {
    private int id;
    private TipoCarga tipo;
    private Estado estado;
    private int tempoTotalNecessario;
    private int tempoExecutado    = 0;
    private int tempoNoDisco      = 0;  // Critério 1 do Nível 2: tempo desde o último swap
    private int tempoNaMemoria    = 0;  // Tempo acumulado de espera na RAM
    private int tempoInativoRAM   = 0;  // Critério 2 do Nível 2: ciclos sem usar a CPU

    public Processo(int id, TipoCarga tipo, int tempoTotalNecessario) {
        this.id = id;
        this.tipo = tipo;
        this.tempoTotalNecessario = tempoTotalNecessario;
        this.estado = Estado.PRONTO;
    }

    public Processo(Tarefa tarefa) {
        this.id = tarefa.getId();
        this.tipo = tarefa.getTipo();
        this.tempoTotalNecessario = tarefa.getTempoTotalNecessario();
        this.estado = Estado.PRONTO;
    }


    /** Envelhece o processo enquanto está no disco (aging anti-starvation). */
    public void envelhecerDisco(int delta) {
        tempoNoDisco += delta;
    }

    /** Envelhece o processo enquanto espera na RAM sem usar a CPU. */
    public void envelhecerMemoria(int delta) {
        tempoNaMemoria += delta;
    }

    /** Incrementa o contador de inatividade na RAM (sem CPU). */
    public void incrementarInativoRAM() {
        tempoInativoRAM++;
    }

    /** Registra o uso da CPU pelo processo. */
    public void executar(int quantum) {
        tempoExecutado += quantum;
    }

    public boolean isConcluido() {
        return tempoExecutado >= tempoTotalNecessario;
    }

    public int getTempoRestante() {
        return Math.max(0, tempoTotalNecessario - tempoExecutado);
    }

    // ── Getters e Setters ──────────────────────────────────────────────────
    public int getId()                        { return id; }
    public TipoCarga getTipo()                { return tipo; }
    public Estado getEstado()                 { return estado; }
    public int getTempoExecutado()            { return tempoExecutado; }
    public int getTempoTotalNecessario()      { return tempoTotalNecessario; }
    public int getTempoNoDisco()              { return tempoNoDisco; }
    public int getTempoNaMemoria()            { return tempoNaMemoria; }
    public int getTempoInativoRAM()           { return tempoInativoRAM; }

    public void setEstado(Estado estado)                { this.estado = estado; }
    public void setTempoNoDisco(int v)                  { this.tempoNoDisco = v; }
    public void setTempoNaMemoria(int v)                { this.tempoNaMemoria = v; }
    public void setTempoInativoRAM(int v)               { this.tempoInativoRAM = v; }

    @Override
    public String toString() {
        String t = (tipo == TipoCarga.LIMITADO_CPU) ? "CPU" : "E/S";
        return String.format("P%d[%s](%d/%d)", id, t, Math.min(tempoExecutado, tempoTotalNecessario), tempoTotalNecessario);
    }
}
