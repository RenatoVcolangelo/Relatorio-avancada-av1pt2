package io.sim;

public class ExcelBanco extends Thread{
    // thread que atualiza a planilha com os dados das transações
    Banco banco;
    public ExcelBanco(Banco banco){
        this.banco = banco;
    }

    @Override
    public void run(){
        // long t0 = System.nanoTime();
        // System.out.println("run excel = " + t0);
        try{

        while(Banco.ativo){
            if(!Banco.transacoes.isEmpty()){
                Relatorio.manipulaExcelTransacao(Banco.transacoes.remove(0));
            }
            Thread.sleep(100);
            // long t1 = System.nanoTime();
        	// System.out.println("fim excel = " + t1);
        }

    } catch(Exception e){
        
    }
    }
}
