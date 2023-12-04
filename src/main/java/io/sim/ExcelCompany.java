package io.sim;

public class ExcelCompany extends Thread{
    // thread que atualiza a planilha com os dados dos carros
    Company company;
    public ExcelCompany(Company company){
        this.company = company;
    }

    @Override
    public void run(){
        // long t0 = System.nanoTime();
        // System.out.println("run excel = " + t0);
        try{

        while(Company.ativo){
            // enquanto existir dados para escrever
            if(!Company.reportData.isEmpty()){
                Relatorio.manipulaExcelAuto(Company.reportData.remove(0));
            }
            Thread.sleep(100);
            // long t1 = System.nanoTime();
            // System.out.println("fim excel = " + t1);
        }

    } catch(Exception e){

    }
    }
}
