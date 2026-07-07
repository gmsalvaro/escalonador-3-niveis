package org.example.Simulador;

import org.example.escalonador.*;
import org.example.model.*;

public class Simulador {

    private static final int QUANTUM = 3;
    private static final int TAMANHO_MEMORIA = 2; // RAM menor para forçar swapping e facilitar didática
    private static final int PAUSA_NIVEL_MS = 250; // pausa entre níveis menor para fluidez

    private final FilaDeEntrada filaDeEntrada = new FilaDeEntrada();
    private final MemoriaRAM memoriaRAM = new MemoriaRAM(TAMANHO_MEMORIA);
    private final ArmazenamentoSwap armazenamentoSwap = new ArmazenamentoSwap();
    private final FilaDeProntos filaDeProntos = new FilaDeProntos();
    private final Processador processador = new Processador();

    private final EscalonadorAdmissao escalonadorAdmissao = new EscalonadorAdmissao();
    private final EscalonadorMemoria escalonadorMemoria = new EscalonadorMemoria();
    private final EscalonadorCPU escalonadorCPU = new EscalonadorCPU(QUANTUM);

    private int tempo = 0;

    // Cores ANSI para didática no console
    private static final String RESET = "\u001B[0m";
    private static final String NEGRITO = "\u001B[1m";
    private static final String VERMELHO = "\u001B[31m";
    private static final String VERDE = "\u001B[32m";
    private static final String AMARELO = "\u001B[33m";
    private static final String AZUL = "\u001B[34m";
    private static final String ROXO = "\u001B[35m";
    private static final String CIANO = "\u001B[36m";

    // LOOP PRINCIPAL
    public void executarCiclo() throws InterruptedException {
        tempo++;

        Thread.sleep(PAUSA_NIVEL_MS);
        String logAdmissao = escalonadorAdmissao.admitir(filaDeEntrada, memoriaRAM, filaDeProntos);

        Thread.sleep(PAUSA_NIVEL_MS);
        String logSwap = escalonadorMemoria.gerenciarSwap(memoriaRAM, armazenamentoSwap, filaDeProntos, filaDeEntrada);

        Thread.sleep(PAUSA_NIVEL_MS);
        String logCPU = escalonadorCPU.escalonar(filaDeProntos, memoriaRAM, processador);

        Thread.sleep(PAUSA_NIVEL_MS);
        imprimirRelatorio(logAdmissao, logSwap, logCPU);
    }

    public void adicionarJob(Tarefa tarefa) {
        filaDeEntrada.adicionar(tarefa);
        System.out.println("  + " + tarefa + " adicionada a fila de entrada.");
    }

    public boolean isFinalizado() {
        return filaDeEntrada.isEmpty() && armazenamentoSwap.isEmpty() && memoriaRAM.isEmpty();
    }

    // VISUALIZACAO DIDATICA COLORIDA
    private void imprimirRelatorio(String admissao, String swap, String cpu) {
        String divLarga = "==========================================================";
        String divCurta = "----------------------------------------------------------";

        System.out.println(NEGRITO + CIANO + divLarga + RESET);
        System.out.printf(NEGRITO + CIANO + "  CICLO %02d%n" + RESET, tempo);
        System.out.println(NEGRITO + CIANO + divCurta + RESET);
        
        System.out.println("  " + NEGRITO + "N1 - ADMISSAO : " + RESET + colorirAdmissao(admissao));
        System.out.println("  " + NEGRITO + "N2 - MEMORIA  : " + RESET + colorirSwap(swap));
        System.out.println("  " + NEGRITO + "N3 - CPU      : " + RESET + colorirCPU(cpu));
        
        System.out.println(NEGRITO + CIANO + divCurta + RESET);
        System.out.println(NEGRITO + "  ESTADO DAS ESTRUTURAS:" + RESET);
        System.out.println("  " + NEGRITO + CIANO + "Fila Entrada : " + RESET + CIANO + listarTarefas(filaDeEntrada.getFila()) + RESET);
        System.out.println("  " + NEGRITO + ROXO + "Disco Swap   : " + RESET + ROXO + listarDisco() + RESET);
        System.out.println("  " + NEGRITO + AZUL + "Memoria RAM  : " + RESET + AZUL + listarRAM() + RESET);
        System.out.println(NEGRITO + CIANO + divLarga + RESET);
        System.out.println();
    }

    private String colorirAdmissao(String msg) {
        if (msg.contains("Admitido")) {
            return VERDE + msg + RESET;
        } else if (msg.contains("Suspensa")) {
            return VERMELHO + msg + RESET;
        } else {
            return AZUL + msg + RESET;
        }
    }

    private String colorirSwap(String msg) {
        if (msg.contains("Swap-OUT") || msg.contains("Swap-IN")) {
            return ROXO + msg + RESET;
        } else {
            return AZUL + msg + RESET;
        }
    }

    private String colorirCPU(String msg) {
        if (msg.contains("Concluido")) {
            return NEGRITO + VERDE + msg + RESET;
        } else if (msg.contains("Executando")) {
            return VERDE + msg + RESET;
        } else if (msg.contains("Bloqueado")) {
            return VERMELHO + msg + RESET;
        } else {
            return AMARELO + msg + RESET;
        }
    }

    private String listarTarefas(Iterable<Tarefa> lista) {
        StringBuilder sb = new StringBuilder();
        for (Tarefa t : lista) {
            String tTipo = (t.getTipo() == TipoCarga.LIMITADO_CPU) ? "CPU" : "E/S";
            sb.append(String.format("P%d[%s](0/%d)", t.getId(), tTipo, t.getTempoTotalNecessario())).append("  ");
        }
        String s = sb.toString().trim();
        return s.isEmpty() ? "(vazia)" : s;
    }

    private String listarDisco() {
        if (armazenamentoSwap.isEmpty())
            return "(vazio)";
        StringBuilder sb = new StringBuilder();
        for (Processo p : armazenamentoSwap.getProcessos())
            sb.append(p).append("[d:").append(p.getTempoNoDisco()).append("]  ");
        return sb.toString().trim();
    }

    private String listarRAM() {
        if (memoriaRAM.isEmpty())
            return "(vazia)";
        StringBuilder sb = new StringBuilder();
        for (Processo p : memoriaRAM.getProcessos())
            sb.append(p).append("[i:").append(p.getTempoInativoRAM()).append("]  ");
        return sb.toString().trim();
    }
}
