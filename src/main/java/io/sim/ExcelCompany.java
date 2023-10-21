package io.sim;

public class ExcelCompany extends Thread{
    
    Company company;
    public ExcelCompany(Company company){
        this.company = company;
    }

    @Override
    public void run(){
        try{

        while(Company.ativo){
            if(!Company.reportData.isEmpty()){
                Relatorio.manipulaExcelAuto(Company.reportData.remove(0));
            }
            Thread.sleep(100);
        }

    } catch(Exception e){

    }
    }
}
