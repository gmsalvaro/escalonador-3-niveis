package org.example.escalonador;

import org.example.model.ArmazenamentoSwap;
import org.example.model.FilaDeEntrada;
import org.example.model.FilaDeProntos;
import org.example.model.MemoriaRAM;
import org.example.model.Processo;
import org.example.model.TipoCarga;

public class EscalonadorMemoria {

    public String gerenciarSwap(MemoriaRAM memoriaRAM, ArmazenamentoSwap armazenamentoSwap, FilaDeProntos filaDeProntos, FilaDeEntrada filaDeEntrada) {
        // Envelhece processos no disco de swap
        armazenamentoSwap.envelhecerProcessos(1);

        int capacidadeMaxima = memoriaRAM.getCapacidadeMaxima();
        StringBuilder log = new StringBuilder();
        boolean houveSwapOut = false;

        Processo out = null;

        // --- Swap-OUT ---
        // Ativado se RAM estiver cheia e houver processos pendentes (seja no swap ou na fila de entrada)
        if (memoriaRAM.size() >= capacidadeMaxima && (!armazenamentoSwap.isEmpty() || !filaDeEntrada.isEmpty())) {
            for (Processo p : memoriaRAM.getProcessos()) {
                if (out == null || p.getTempoInativoRAM() > out.getTempoInativoRAM()) {
                    out = p;
                }
            }
            if (out != null) {
                int inatividade = out.getTempoInativoRAM();
                memoriaRAM.remover(out);
                filaDeProntos.remover(out); // Sincroniza com a FilaDeProntos
                out.setTempoInativoRAM(0);
                armazenamentoSwap.adicionar(out);
                log.append("Swap-OUT: ").append(out).append(" -> Disco (inativo ha ").append(inatividade).append(" ciclos)");
                houveSwapOut = true;
            }
        }

        // --- Swap-IN ---
        if (!armazenamentoSwap.isEmpty() && memoriaRAM.size() < capacidadeMaxima) {
            int cpuNaRam = memoriaRAM.contarPorTipo(TipoCarga.LIMITADO_CPU);
            int esNaRam = memoriaRAM.size() - cpuNaRam;
            TipoCarga tipoIdeal = (cpuNaRam <= esNaRam) ? TipoCarga.LIMITADO_CPU : TipoCarga.LIMITADO_ES;

            Processo in = null;
            for (Processo p : armazenamentoSwap.getProcessos()) {
                if (p != out && p.getTipo() == tipoIdeal) {
                    if (in == null || p.getTempoNoDisco() > in.getTempoNoDisco()) {
                        in = p;
                    }
                }
            }
            if (in == null) {
                for (Processo p : armazenamentoSwap.getProcessos()) {
                    if (p != out && (in == null || p.getTempoNoDisco() > in.getTempoNoDisco())) {
                        in = p;
                    }
                }
            }

            if (in != null) {
                int tempoDisco = in.getTempoNoDisco();
                armazenamentoSwap.remover(in);
                in.setTempoNoDisco(0);
                memoriaRAM.adicionar(in);
                filaDeProntos.adicionar(in); // Sincroniza com a FilaDeProntos
                
                if (houveSwapOut) {
                    log.append("\n                  ");
                }
                log.append("Swap-IN:  ").append(in).append(" <- Disco (no disco ha ").append(tempoDisco)
                   .append(" ciclos, ideal=").append(tipoLabel(tipoIdeal)).append(")");
            }
        }

        if (log.length() == 0) {
            return "Sem alteracoes de swap";
        }
        return log.toString();
    }

    private String tipoLabel(TipoCarga t) {
        return t == TipoCarga.LIMITADO_CPU ? "CPU" : "E/S";
    }
}
