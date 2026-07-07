package org.example;

import org.example.Simulador.Simulador;
import org.example.model.Tarefa;
import org.example.model.TipoCarga;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("========================================================");
        System.out.println("  SIMULACAO DE ESCALONAMENTO EM TRES NIVEIS");
        System.out.println("  Baseado em Tanenbaum, Sistemas Operacionais Cap. 2");
        System.out.println("========================================================");
        System.out.println();

        Simulador simulador = new Simulador();

        System.out.println("  Jobs chegando ao sistema:");
        System.out.println();
        // 6 processos (3 CPU-bound, 3 E/S-bound) variados para simulação didática compacta
        simulador.adicionarJob(new Tarefa(1, TipoCarga.LIMITADO_CPU, 6));
        simulador.adicionarJob(new Tarefa(2, TipoCarga.LIMITADO_ES, 4));
        simulador.adicionarJob(new Tarefa(3, TipoCarga.LIMITADO_CPU, 8));
        simulador.adicionarJob(new Tarefa(4, TipoCarga.LIMITADO_ES, 6));
        simulador.adicionarJob(new Tarefa(5, TipoCarga.LIMITADO_CPU, 4));
        simulador.adicionarJob(new Tarefa(6, TipoCarga.LIMITADO_ES, 8));

        Thread.sleep(1000);

        while (!simulador.isFinalizado()) {
            simulador.executarCiclo();
            Thread.sleep(2000); // pausa de 2 segundos entre ciclos para leitura
        }

        System.out.println();
        System.out.println("========================================================");
        System.out.println("  SIMULACAO CONCLUIDA! Todos os processos finalizados.");
        System.out.println("========================================================");
    }
}