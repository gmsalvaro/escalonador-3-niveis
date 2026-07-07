package org.example.Simulador;

import org.example.model.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Simulador {

    private final Queue<Processo> filaDeEntrada = new LinkedList<>();
    private final List<Processo> memoriaRAM = new ArrayList<>();
    private final List<Processo> disco = new ArrayList<>();

    private static final int QUANTUM = 3;
    private static final int TAMANHO_MEMORIA = 4;
    private static final int PAUSA_NIVEL_MS = 600; // pausa entre niveis

    private int tempo = 0;

    // =========================================================
    // NIVEL 1 - ESCALONADOR DE ADMISSAO
    // Fila de Entrada -> Memoria Principal
    // Algoritmo: Mix de Carga (equilibra CPU-bound / E/S-bound)
    // =========================================================
    private void escalonadorDeAdmissao() {
        println("[N1 - ADMISSAO]");

        if (filaDeEntrada.isEmpty()) {
            println("    Fila de entrada vazia.");
            return;
        }
        if (memoriaRAM.size() >= TAMANHO_MEMORIA) {
            println("    RAM cheia (" + memoriaRAM.size() + "/" + TAMANHO_MEMORIA
                    + ") - admissao suspensa.");
            return;
        }

        int cpuNaRam = contarTipo(memoriaRAM, TipoCarga.LIMITADO_CPU);
        int esNaRam = memoriaRAM.size() - cpuNaRam;
        TipoCarga tipoIdeal = (cpuNaRam <= esNaRam) ? TipoCarga.LIMITADO_CPU : TipoCarga.LIMITADO_ES;

        Processo escolhido = null;
        for (Processo p : filaDeEntrada) {
            if (p.getTipo() == tipoIdeal) {
                escolhido = p;
                break;
            }
        }
        boolean fallback = false;
        if (escolhido == null) {
            escolhido = filaDeEntrada.peek();
            fallback = true;
        }

        filaDeEntrada.remove(escolhido);
        escolhido.setTempoInativoRAM(0);
        memoriaRAM.add(escolhido);

        String motivo = fallback
                ? "(fallback FIFO - tipo ideal indisponivel)"
                : "(mix " + cpuNaRam + "CPU/" + esNaRam + "ES -> precisava " + tipoLabel(tipoIdeal) + ")";
        println("    " + escolhido + " -> RAM  " + motivo);
    }

    // =========================================================
    // NIVEL 2 - ESCALONADOR DA MEMORIA (Swap)
    // Memoria <-> Disco
    // Swap-OUT: processo mais inativo (criterio 2 do livro)
    // Swap-IN : mais velho no disco + Mix de Carga (criterio 1)
    // =========================================================
    private void escalonadorDaMemoria() {
        println("[N2 - MEMORIA / SWAP]");

        for (Processo p : disco)
            p.envelhecerDisco(1);

        // --- Swap-OUT ---
        if (memoriaRAM.size() > TAMANHO_MEMORIA) {
            Processo out = null;
            for (Processo p : memoriaRAM)
                if (out == null || p.getTempoInativoRAM() > out.getTempoInativoRAM())
                    out = p;
            if (out != null) {
                int inatividade = out.getTempoInativoRAM();
                memoriaRAM.remove(out);
                out.setTempoInativoRAM(0);
                disco.add(out);
                println("    Swap-OUT: " + out + " -> disco  (inativo ha " + inatividade + " ciclos)");
            }
        } else {
            println("    RAM " + memoriaRAM.size() + "/" + TAMANHO_MEMORIA
                    + " - sem Swap-OUT necessario.");
        }

        // --- Swap-IN ---
        if (!disco.isEmpty() && memoriaRAM.size() < TAMANHO_MEMORIA) {
            int cpuNaRam = contarTipo(memoriaRAM, TipoCarga.LIMITADO_CPU);
            int esNaRam = memoriaRAM.size() - cpuNaRam;
            TipoCarga tipoIdeal = (cpuNaRam <= esNaRam) ? TipoCarga.LIMITADO_CPU : TipoCarga.LIMITADO_ES;

            Processo in = null;
            for (Processo p : disco)
                if (p.getTipo() == tipoIdeal)
                    if (in == null || p.getTempoNoDisco() > in.getTempoNoDisco())
                        in = p;
            if (in == null)
                for (Processo p : disco)
                    if (in == null || p.getTempoNoDisco() > in.getTempoNoDisco())
                        in = p;

            int tempoDisco = in.getTempoNoDisco();
            disco.remove(in);
            in.setTempoNoDisco(0);
            memoriaRAM.add(in);
            println("    Swap-IN : " + in + " <- disco  (no disco ha " + tempoDisco
                    + " ciclos, ideal=" + tipoLabel(tipoIdeal) + ")");
        } else if (disco.isEmpty()) {
            println("    Disco vazio - sem Swap-IN.");
        } else {
            println("    RAM cheia - sem Swap-IN neste ciclo.");
        }
    }

    // =========================================================
    // NIVEL 3 - ESCALONADOR DA CPU (Round-Robin)
    // Seleciona o proximo processo pronto e executa 1 Quantum.
    // =========================================================
    private void escalonadorDaCpu() {
        println("[N3 - CPU  Round-Robin  Q=" + QUANTUM + "]");

        if (memoriaRAM.isEmpty()) {
            println("    CPU ociosa.");
            return;
        }

        Processo proc = memoriaRAM.remove(0);

        for (Processo p : memoriaRAM) {
            p.envelhecerMemoria(QUANTUM);
            p.incrementarInativoRAM();
        }

        if (proc.getEstado() == Estado.BLOQUEADO) {
            memoriaRAM.add(proc);
            println("    " + proc + " BLOQUEADO (E/S pendente) -> fim da fila.");
            return;
        }

        int antes = proc.getTempoExecutado();
        proc.executar(QUANTUM);
        proc.setTempoNaMemoria(0);
        proc.setTempoInativoRAM(0);

        if (proc.isConcluido()) {
            proc.setEstado(Estado.CONCLUIDO);
            println("    Executando " + proc + " ... CONCLUIDO! Removido do sistema.");
        } else {
            memoriaRAM.add(proc);
            println("    Executando " + proc + " ... restam " + proc.getTempoRestante()
                    + " unidades. Volta ao fim da fila.");
        }
    }

    // =========================================================
    // LOOP PRINCIPAL
    // =========================================================
    public void executarCiclo() throws InterruptedException {
        tempo++;
        imprimirCabecalhoCiclo();

        Thread.sleep(PAUSA_NIVEL_MS);
        escalonadorDeAdmissao();

        Thread.sleep(PAUSA_NIVEL_MS);
        escalonadorDaMemoria();

        Thread.sleep(PAUSA_NIVEL_MS);
        escalonadorDaCpu();

        Thread.sleep(PAUSA_NIVEL_MS);
        imprimirSnapshot();
    }

    public void adicionarJob(Processo processo) {
        filaDeEntrada.offer(processo);
        System.out.println("  + " + processo + " adicionado a fila de entrada.");
    }

    public boolean isFinalizado() {
        return filaDeEntrada.isEmpty() && disco.isEmpty() && memoriaRAM.isEmpty();
    }

    // =========================================================
    // VISUALIZACAO
    // =========================================================
    private void imprimirCabecalhoCiclo() {
        String linha = "=".repeat(56);
        System.out.println();
        System.out.println(linha);
        System.out.printf("  CICLO %-3d%n", tempo);
        System.out.println(linha);
    }

    private void imprimirSnapshot() {
        System.out.println();
        System.out.println("  +-----------------+--------------------------------------+");
        System.out.printf("  | Fila de Entrada | %-38s|%n", listar(filaDeEntrada));
        System.out.printf("  | Disco (swap)    | %-38s|%n", listarDisco());
        System.out.printf("  | RAM             | %-38s|%n", listarRAM());
        System.out.println("  +-----------------+--------------------------------------+");
    }

    private String listar(Iterable<Processo> lista) {
        StringBuilder sb = new StringBuilder();
        for (Processo p : lista)
            sb.append(p).append("  ");
        String s = sb.toString().trim();
        return s.isEmpty() ? "(vazia)" : s;
    }

    private String listarDisco() {
        if (disco.isEmpty())
            return "(vazio)";
        StringBuilder sb = new StringBuilder();
        for (Processo p : disco)
            sb.append(p).append("[d:").append(p.getTempoNoDisco()).append("]  ");
        return sb.toString().trim();
    }

    private String listarRAM() {
        if (memoriaRAM.isEmpty())
            return "(vazia)";
        StringBuilder sb = new StringBuilder();
        for (Processo p : memoriaRAM)
            sb.append(p).append("[i:").append(p.getTempoInativoRAM()).append("]  ");
        return sb.toString().trim();
    }

    private void println(String msg) {
        System.out.println(msg);
    }

    private int contarTipo(List<Processo> lista, TipoCarga tipo) {
        int count = 0;
        for (Processo p : lista)
            if (p.getTipo() == tipo)
                count++;
        return count;
    }

    private String tipoLabel(TipoCarga t) {
        return t == TipoCarga.LIMITADO_CPU ? "CPU" : "E/S";
    }
}
