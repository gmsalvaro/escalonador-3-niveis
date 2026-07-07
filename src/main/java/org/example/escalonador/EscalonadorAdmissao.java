package org.example.escalonador;

import org.example.model.FilaDeEntrada;
import org.example.model.FilaDeProntos;
import org.example.model.MemoriaRAM;
import org.example.model.Processo;
import org.example.model.Tarefa;
import org.example.model.TipoCarga;

public class EscalonadorAdmissao {

    public String admitir(FilaDeEntrada filaDeEntrada, MemoriaRAM memoriaRAM, FilaDeProntos filaDeProntos) {
        if (filaDeEntrada.isEmpty()) {
            return "Fila de entrada vazia";
        }
        if (memoriaRAM.isCheia()) {
            return "Suspensa (RAM cheia: " + memoriaRAM.size() + "/" + memoriaRAM.getCapacidadeMaxima() + ")";
        }

        int cpuNaRam = memoriaRAM.contarPorTipo(TipoCarga.LIMITADO_CPU);
        int esNaRam = memoriaRAM.size() - cpuNaRam;
        TipoCarga tipoIdeal = (cpuNaRam <= esNaRam) ? TipoCarga.LIMITADO_CPU : TipoCarga.LIMITADO_ES;

        Tarefa escolhida = null;
        for (Tarefa t : filaDeEntrada.getFila()) {
            if (t.getTipo() == tipoIdeal) {
                escolhida = t;
                break;
            }
        }
        boolean fallback = false;
        if (escolhida == null) {
            escolhida = filaDeEntrada.peek();
            fallback = true;
        }

        filaDeEntrada.remove(escolhida);

        // Instancia o processo
        Processo novoProcesso = new Processo(escolhida);
        novoProcesso.setTempoInativoRAM(0);
        
        memoriaRAM.adicionar(novoProcesso);
        filaDeProntos.adicionar(novoProcesso);

        String motivo = fallback
                ? "(fallback FIFO - tipo ideal indisponivel)"
                : "(mix " + cpuNaRam + "CPU/" + esNaRam + "ES -> precisava " + tipoLabel(tipoIdeal) + ")";
        return "Admitido: " + novoProcesso + " " + motivo;
    }

    private String tipoLabel(TipoCarga t) {
        return t == TipoCarga.LIMITADO_CPU ? "CPU" : "E/S";
    }
}
