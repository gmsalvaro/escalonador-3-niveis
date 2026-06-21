package org.example;

import org.example.Simulador.Simulador;
import org.example.model.Processo;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando a Simulação de Escalonamento em 3 Níveis\n");
        Simulador simulador = new Simulador();

        simulador.adicionarProcesso(new Processo(1, 10));
        simulador.adicionarProcesso(new Processo(2, 4));
        simulador.adicionarProcesso(new Processo(3, 15));
        simulador.adicionarProcesso(new Processo(4, 2));
        simulador.adicionarProcesso(new Processo(5, 8));
        simulador.adicionarProcesso(new Processo(6, 6));
        simulador.adicionarProcesso(new Processo(7, 3));

        System.out.println("\n--- Início dos Ciclos de Escalonamento ---");
        while (!simulador.isFinalizado()) {
            simulador.executarCiclo();

            // Pausa rápida apenas para visualizar melhor a saída (simulando tempo real)
            try {
                Thread.sleep(500); // Meio segundo por ciclo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n--- Simulação Concluída! Todos os processos foram finalizados. ---");
    }
}