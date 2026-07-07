package org.example.model;

public class Processador {
    
    /**
     * Executa o processo pelo quantum especificado.
     * 
     * @param processo O processo a ser executado.
     * @param quantum O quantum de tempo de CPU.
     */
    public void executar(Processo processo, int quantum) {
        processo.executar(quantum);
    }
}
