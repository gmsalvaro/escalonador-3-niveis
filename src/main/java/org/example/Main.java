package org.example;

import org.example.Simulador.Simulador;
import org.example.model.Processo;
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
        // 18 processos (9 CPU-bound, 9 E/S-bound) variados
        simulador.adicionarJob(new Processo(1, TipoCarga.LIMITADO_CPU, 9));
        simulador.adicionarJob(new Processo(2, TipoCarga.LIMITADO_ES, 6));
        simulador.adicionarJob(new Processo(3, TipoCarga.LIMITADO_CPU, 12));
        simulador.adicionarJob(new Processo(4, TipoCarga.LIMITADO_ES, 9));
        simulador.adicionarJob(new Processo(5, TipoCarga.LIMITADO_CPU, 6));
        simulador.adicionarJob(new Processo(6, TipoCarga.LIMITADO_ES, 15));
        simulador.adicionarJob(new Processo(7, TipoCarga.LIMITADO_CPU, 3));
        simulador.adicionarJob(new Processo(8, TipoCarga.LIMITADO_ES, 12));
        simulador.adicionarJob(new Processo(9, TipoCarga.LIMITADO_CPU, 9));
        simulador.adicionarJob(new Processo(10, TipoCarga.LIMITADO_ES, 6));
        simulador.adicionarJob(new Processo(11, TipoCarga.LIMITADO_CPU, 6));
        simulador.adicionarJob(new Processo(12, TipoCarga.LIMITADO_ES, 9));
        simulador.adicionarJob(new Processo(13, TipoCarga.LIMITADO_CPU, 15));
        simulador.adicionarJob(new Processo(14, TipoCarga.LIMITADO_ES, 3));
        simulador.adicionarJob(new Processo(15, TipoCarga.LIMITADO_CPU, 9));
        simulador.adicionarJob(new Processo(16, TipoCarga.LIMITADO_ES, 12));
        simulador.adicionarJob(new Processo(17, TipoCarga.LIMITADO_CPU, 6));
        simulador.adicionarJob(new Processo(18, TipoCarga.LIMITADO_ES, 9));

        Thread.sleep(3500);

        while (!simulador.isFinalizado()) {
            simulador.executarCiclo();
            Thread.sleep(1500); // pausa entre ciclos
        }

        System.out.println();
        System.out.println("========================================================");
        System.out.println("  SIMULACAO CONCLUIDA! Todos os processos finalizados.");
        System.out.println("========================================================");
    }
}