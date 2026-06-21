package org.example.Simulador;

import org.example.model.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

import org.example.model.Processo;

public class Simulador {
    private Queue<Processo> filaDeEntrada = new LinkedList<>();
    private List<Processo> disco = new ArrayList<>();
    private Queue<Processo> memoria = new LinkedList<>();

    private final int Quantum = 3;
    private final int inSwap = 5;
    private final int outSwap = 8;

    // Numero maximo de processos na memoria RAM
    private final int TAMANHO_MEMORIA = 5;
    // Tempo do relogio
    private int tempo = 0;

    // Nivel 1: Escalonador de Admissão
    // Decide quem entra no sistema (Fila de Entrada-> Disco)
    private void escalonadorDeAdmissao() {
        // Verifica se a fila de entrada nao está vazia
        if (!filaDeEntrada.isEmpty()) {
            // Remove o processo da fila de entrada
            Processo novoJob = filaDeEntrada.poll();
            // Adiciona o processo no disco
            disco.add(novoJob);
            System.out.println("[Admissão] Job P" + novoJob.getId() + " admitido no sistema (Disco).");
        }
    }

    // Nivel 2: Escalonador de Swapping (Disco -> Memoria)
    // Decide quais processos vao para a memoria RAM
    // Escalonador Fifo (First In First Out).
    // Regra: Se houver espaço, traz o processo mais velho do disco. Se não houver,
    // troca o mais velho da memória pelo mais velho do disco.
    private void escalonadorDeSwapping() {
        if (disco.isEmpty()) {
            return;
        }

        // Envelhecer todos os processos no disco
        for (Processo processo : disco) {
            processo.envelhecerDisco(inSwap);
        }

        // Se a memória estiver cheia e o disco tem processos, fazemos o Swap OUT do mais velho da memória
        if (memoria.size() >= TAMANHO_MEMORIA) {
            Processo maisVelhoMemoria = null;
            for (Processo p : memoria) {
                if (maisVelhoMemoria == null || p.getTempoNaMemoria() > maisVelhoMemoria.getTempoNaMemoria()) {
                    maisVelhoMemoria = p;
                }
            }
            if (maisVelhoMemoria != null) {
                memoria.remove(maisVelhoMemoria);
                maisVelhoMemoria.setTempoNaMemoria(0); // Reseta o tempo da memoria
                disco.add(maisVelhoMemoria);
                System.out.println("[Memória] Swap OUT: P" + maisVelhoMemoria.getId() + " movido da Memória para o Disco.");
            }
        }

        // Com espaço na memória (seja por conclusão ou Swap OUT), faz o Swap IN do mais velho no disco
        while (memoria.size() < TAMANHO_MEMORIA && !disco.isEmpty()) {
            Processo escolhido = disco.get(0);
            for (Processo p : disco) {
                if (p.getTempoNoDisco() > escolhido.getTempoNoDisco()) {
                    escolhido = p;
                }
            }

            disco.remove(escolhido);
            escolhido.setTempoNoDisco(0); // reseta o contador
            memoria.offer(escolhido);
            System.out.println("[Memória] Swap IN: P" + escolhido.getId() + " movido do Disco para a Memória.");
        }
    }

    // Nivel 3: Escalonador de Memoria (RAM -> CPU)
    // Escalonador Round Robin (Quantum fixo).
    // Regra: Todos os processos tem um tempo fixo de execucao por quantum.
    // Se o processo nao terminar no quantum, ele volta para o final da memoria.
    public void escalonadorDaCpu() {
        if (memoria.isEmpty()) {
            System.out.println("[CPU] Ociosa...");
            return;
        }

        // Executa apenas o processo na cabeca da fila por 1 Quantum (Round-Robin)
        Processo processo = memoria.poll();
        
        // Envelhece apenas os processos que ficaram esperando na memória
        for (Processo p : memoria) {
            p.envelhecerMemoria(outSwap);
        }

        if (processo.getEstado() == Estado.BLOQUEADO) {
            memoria.offer(processo); // Devolve para o final da fila se estiver bloqueado
            return;
        }

        System.out.println("[CPU] Executando " + processo);

        // Simula o trabalho do quantum
        processo.tempoExecutado(Quantum);
        processo.setTempoNaMemoria(0); // Reseta o tempo de espera na memória, pois acabou de usar a CPU
        
        // Verifica se terminou
        if (processo.isConcluido()) {
            processo.setEstado(Estado.CONCLUÍDO);
            System.out.println(">>> Processo P" + processo.getId() + " CONCLUÍDO e removido do sistema. <<<");
        } else {
            // Se não terminou, volta para o fim da fila da memória (Round-Robin)
            memoria.offer(processo);
        }
    }

    public void adicionarProcesso(Processo processo) {
        filaDeEntrada.offer(processo);
        System.out.println("[Sistema] Processo P" + processo.getId() + " chegou na Fila de Entrada.");
    }

    public void executarCiclo() {
        tempo++;
        System.out.println("\n--- Ciclo de Tempo: " + tempo + " ---");
        escalonadorDeAdmissao();
        escalonadorDeSwapping();
        escalonadorDaCpu();
    }

    public boolean isFinalizado() {
        return filaDeEntrada.isEmpty() && disco.isEmpty() && memoria.isEmpty();
    }
}
