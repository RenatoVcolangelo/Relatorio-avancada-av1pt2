package io.sim;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Relatorio {
    // Nomes dos arquivos para relatórios de automóveis e transações
    private static final String fileNameAuto = "RelatorioAuto.xlsx";
    private static final String fileNameTransacao = "RelatorioTransacao.xlsx";
    //private static final String fileNameRec = "Reconciliacao_distacia.xlsx";
    private static final String fileNameRec = "Recon.xlsx";
    // Método para criar um arquivo Excel para relatório de automóveis
    public static synchronized void criaExcelAuto(){
        try{


            Workbook workbook = new XSSFWorkbook();

            // Crie uma planilha
            Sheet sheet = workbook.createSheet();
            int lastRow = 0;
            Row row = sheet.createRow(lastRow);

            // Cria o cabeçalho
            Cell TimeStamp = row.createCell(0);
            TimeStamp.setCellValue("TimeStamp");
            Cell CarID = row.createCell(1);
            CarID.setCellValue("CarID");
            Cell RoadID= row.createCell(2);
            RoadID.setCellValue("RoadID");
            Cell RuteID= row.createCell(3);
            RuteID.setCellValue("ROuteID");
            Cell Speed = row.createCell(4);
            Speed.setCellValue("Speed");
            Cell Odometro = row.createCell(5);
            Odometro.setCellValue("Odometro");
            Cell Consuption = row.createCell(6);
            Consuption.setCellValue("Fuel Consuption");
            Cell Type = row.createCell(7);
            Type.setCellValue("Fuel Type");
            Cell CO2Emission = row.createCell(8);
            CO2Emission.setCellValue("CO2Emission");
            Cell Latitude = row.createCell(9);
            Latitude.setCellValue("Latitude");
            Cell Longitude = row.createCell(10);
            Longitude.setCellValue("Longitude");
            Cell Distance = row.createCell(11);
            Distance.setCellValue("Distance");
            Cell edgeDistance = row.createCell(12);
            edgeDistance.setCellValue("EdgeDistance");


             // Salve o arquivo Excel em disco
            try (FileOutputStream outputStream = new FileOutputStream(fileNameAuto)) {
                workbook.write(outputStream);
                System.out.println("Planilha criada com sucesso!");
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
                
            }

            } catch (Exception e){

    }}

     // Método para criar um arquivo Excel para relatório de transações
     public static synchronized void criaExcelTransacao(){
        try{


            Workbook workbook = new XSSFWorkbook();

            // Crie uma planilha
            Sheet sheet = workbook.createSheet();
            int lastRow = 0;
            Row row = sheet.createRow(lastRow);

            // Cria o cabeçalho
            Cell TimeStamp = row.createCell(0);
            TimeStamp.setCellValue("TimeStamp");
            Cell CarID = row.createCell(1);
            CarID.setCellValue("Conta pagadora");
            Cell RuteID= row.createCell(2);
            RuteID.setCellValue("Conta recebedora");
            Cell Speed = row.createCell(3);
            Speed.setCellValue("Valor");


             // Salve o arquivo Excel em disco
            try (FileOutputStream outputStream = new FileOutputStream(fileNameTransacao)) {
                workbook.write(outputStream);
                System.out.println("Planilha criada com sucesso!");
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
                
            }

            } catch (Exception e){

    }}

         // Método para criar um arquivo Excel para relatório de reconciliacao
     public static synchronized void criaExcelReconciliacao(){
        try{


            Workbook workbook = new XSSFWorkbook();

            // Crie uma planilha
            Sheet sheet = workbook.createSheet();
            int lastRow = 0;
            Row row = sheet.createRow(lastRow);

            // Cria o cabeçalho
            Cell iter = row.createCell(0);
            iter.setCellValue("Iteracao");
            Cell cell1 = row.createCell(1);
            cell1.setCellValue("t1");
            Cell cell2= row.createCell(2);
            cell2.setCellValue("t2");
            Cell cell3 = row.createCell(3);
            cell3.setCellValue("t3");
            Cell cell4 = row.createCell(4);
            cell4.setCellValue("t4");
            Cell cell5 = row.createCell(5);
            cell5.setCellValue("t5");
            Cell cell6 = row.createCell(6);
            cell6.setCellValue("tT");



            Cell cell9 = row.createCell(9);
            cell9.setCellValue("Iteracao");
            Cell cell10 = row.createCell(10);
            cell10.setCellValue("d1");
            Cell cell11= row.createCell(11);
            cell11.setCellValue("d2");
            Cell cell12 = row.createCell(12);
            cell12.setCellValue("d3");
            Cell cell13 = row.createCell(13);
            cell13.setCellValue("d4");
            Cell cell14 = row.createCell(14);
            cell14.setCellValue("d5");
            Cell cell15 = row.createCell(15);
            cell15.setCellValue("dt");


             // Salve o arquivo Excel em disco
            try (FileOutputStream outputStream = new FileOutputStream(fileNameRec)) {
                workbook.write(outputStream);
                System.out.println("Planilha criada com sucesso!");
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
                
            }

            } catch (Exception e){

    }}
    // Método para adicionar dados de automóveis ao arquivo Excel
    public static synchronized void manipulaExcelAuto(DrivingData r){

        try{
            // abre o arquivo ja criado
            FileInputStream inputStream = new FileInputStream(fileNameAuto);
            Workbook workbook = WorkbookFactory.create(inputStream);
            FileOutputStream outputStream = new FileOutputStream(fileNameAuto);
            Sheet sheet = workbook.getSheetAt(0);

            // pega a ultima linha da planilha
            int lastRow = sheet.getLastRowNum();

            Row row = sheet.createRow(lastRow + 1);

            // Crie uma célula e defina seu valor
            Cell cell = row.createCell(0);
            cell.setCellValue(r.getTimeStamp());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(r.getAutoID());
            Cell cell2= row.createCell(2);
            cell2.setCellValue(r.getRoadIDSUMO());
            Cell cell0= row.createCell(3);
            cell0.setCellValue(r.getRouteIDSUMO());
            Cell cell3 = row.createCell(4);
            cell3.setCellValue(r.getSpeed());
            Cell cell4 = row.createCell(5);
            cell4.setCellValue(r.getOdometer());
            Cell cell5 = row.createCell(6);
            cell5.setCellValue(r.getFuelConsumption());
            Cell cell6 = row.createCell(7);
            cell6.setCellValue(r.getFuelType());
            Cell cell7 = row.createCell(8);
            cell7.setCellValue(r.getCo2Emission());
            Cell cell8 = row.createCell(9);
            cell8.setCellValue(r.getLatitude());
            Cell cell9 = row.createCell(10);
            cell9.setCellValue(r.getLongitude());
            Cell cell10 = row.createCell(11);
            cell10.setCellValue(r.getDistance());
            Cell cell11 = row.createCell(12);
            cell11.setCellValue(r.getEdgeDist());
            

            workbook.write(outputStream);
        } catch (IOException e){
                e.printStackTrace();
            }}
    // Método para adicionar dados de transações ao arquivo Excel
     public static synchronized void manipulaExcelTransacao(Transacao t){

        try{
            // abre o arquivo ja criado
            FileInputStream inputStream = new FileInputStream(fileNameTransacao);
            Workbook workbook = WorkbookFactory.create(inputStream);
            FileOutputStream outputStream = new FileOutputStream(fileNameTransacao);
            Sheet sheet = workbook.getSheetAt(0);


            int lastRow = sheet.getLastRowNum();

            Row row = sheet.createRow(lastRow + 1);

            // Crie uma célula e defina seu valor
            Cell cell = row.createCell(0);
            cell.setCellValue(t.getTimestamp());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(t.getContaPagador());
            Cell cell2= row.createCell(2);
            cell2.setCellValue(t.getContaRecebedor());
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(t.getValor());
 
            workbook.write(outputStream);
            
        } catch (IOException e){
                e.printStackTrace();
            }}

        // Método para adicionar dados de reconciliacao arquivo Excel
     public static synchronized void manipulaExcelRec(ArrayList<Double> rec, ArrayList<Double> recd){

        try{
            // abre o arquivo ja criado
            FileInputStream inputStream = new FileInputStream(fileNameRec);
            Workbook workbook = WorkbookFactory.create(inputStream);
            FileOutputStream outputStream = new FileOutputStream(fileNameRec);
            Sheet sheet = workbook.getSheetAt(0);


            int lastRow = sheet.getLastRowNum();

            Row row = sheet.createRow(lastRow + 1);

            // Crie uma célula e defina seu valor
            Cell cell = row.createCell(0);
            cell.setCellValue(lastRow+1);
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(rec.remove(0));
            Cell cell2= row.createCell(2);
            cell2.setCellValue(rec.remove(0));
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(rec.remove(0));
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(rec.remove(0));
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(rec.remove(0));
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(rec.remove(0));


            Cell cel = row.createCell(9);
            cel.setCellValue(lastRow+1);
            Cell cel1 = row.createCell(10);
            cel1.setCellValue(recd.remove(0));
            Cell cel2= row.createCell(11);
            cel2.setCellValue(recd.remove(0));
            Cell cel3 = row.createCell(12);
            cel3.setCellValue(recd.remove(0));
            Cell cel4 = row.createCell(13);
            cel4.setCellValue(recd.remove(0));
            Cell cel5 = row.createCell(14);
            cel5.setCellValue(recd.remove(0));
            Cell cel6 = row.createCell(15);
            cel6.setCellValue(recd.remove(0));

 
            workbook.write(outputStream);
            
        } catch (IOException e){
                e.printStackTrace();
            }}
           
    
}
