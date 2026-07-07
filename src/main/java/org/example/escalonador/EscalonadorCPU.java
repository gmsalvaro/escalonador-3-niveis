package org.example.escalonador;

import org.example.model.FilaDeProntos;
import org.example.model.MemoriaRAM;
import org.example.model.Processador;
import org.example.model.Processo;
import org.example.model.Estado;

public class EscalonadorCPU {
    private final int quantum;

    public EscalonadorCPU(int quantum) {
        this.quantum = quantum;
    }

    public String escalonar(FilaDeProntos filaDeProntos, MemoriaRAM memoriaRAM, Processador processador) {
        if (filaDeProntos.isEmpty()) {
            return "CPU ociosa";
        }

        Processo proc = filaDeProntos.desenfileirar();

        // Incrementa tempo dos outros processos na fila de prontos
        for (Processo p : filaDeProntos.getFila()) {
            p.envelhecerMemoria(quantum);
            p.incrementarInativoRAM();
        }

        if (proc.getEstado() == Estado.BLOQUEADO) {
            filaDeProntos.adicionar(proc);
            return "Bloqueado: " + proc + " (E/S pendente) -> Fim da fila";
        }

        // Executa o processo no Processador
        processador.executar(proc, quantum);
        proc.setTempoNaMemoria(0);
        proc.setTempoInativoRAM(0);

        if (proc.isConcluido()) {
            proc.setEstado(Estado.CONCLUIDO);
            memoriaRAM.remover(proc); // Sincroniza com a RAM física
            return "Concluido: " + proc;
        } else {
            filaDeProntos.adicionar(proc);
            return "Executando: " + proc + " (restam " + proc.getTempoRestante() + " u.t.) -> Fim da fila";
        }
    }
}
