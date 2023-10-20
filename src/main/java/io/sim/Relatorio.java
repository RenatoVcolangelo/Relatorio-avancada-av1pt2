package io.sim;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Relatorio extends Thread{
    private static final String fileName = "Relatorio1.xlsx";

   
    private ArrayList<DrivingData> report;

    public Relatorio(ArrayList<DrivingData> report){
        this.report = report;
    }


    public void run(){
        try{

            //FileInputStream file = new FileInputStream(new File(Relatorio.fileName));

            Workbook workbook = new XSSFWorkbook();

            // Crie uma planilha
            Sheet sheet = workbook.createSheet("relatorio");
            // int lastRow = sheet.getLastRowNum();
            int lastRow = 0;

            Row row = sheet.createRow(lastRow);

            // Crie uma célula e defina seu valor
            Cell TimeStamp = row.createCell(0);
            TimeStamp.setCellValue("TimeStamp");
            Cell CarID = row.createCell(1);
            CarID.setCellValue("CarID");
            Cell RuteID= row.createCell(2);
            RuteID.setCellValue("RuteID");
            Cell Speed = row.createCell(3);
            Speed.setCellValue("Speed");
            Cell Odometro = row.createCell(4);
            Odometro.setCellValue("Odometro");
            Cell Consuption = row.createCell(5);
            Consuption.setCellValue("Fuel Consuption");
            Cell Type = row.createCell(6);
            Type.setCellValue("Fuel Type");
            Cell CO2Emission = row.createCell(7);
            CO2Emission.setCellValue("CO2Emission");
            Cell Latitude = row.createCell(7);
            Latitude.setCellValue("Latitude");
            Cell Longitude = row.createCell(7);
            Longitude.setCellValue("Longitude");


            for(DrivingData r : report){
                lastRow += 1;
                row = sheet.createRow(lastRow);

                // Crie uma célula e defina seu valor
                Cell cell = row.createCell(0);
                cell.setCellValue(r.getTimeStamp());
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(r.getAutoID());
                Cell cell2= row.createCell(2);
                cell2.setCellValue(r.getRouteID());
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(r.getSpeed());
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(r.getOdometer());
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(r.getFuelConsumption());
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(r.getFuelType());
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(r.getCo2Emission());
                Cell cell8 = row.createCell(7);
                cell8.setCellValue(r.getLatitude());
                Cell cell9 = row.createCell(7);
                cell9.setCellValue(r.getLongitude());
            }
  

            // Salve o arquivo Excel em disco
            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                workbook.write(outputStream);
                System.out.println("Planilha criada com sucesso!");
                workbook.close();
            } catch (IOException e) {
                System.out.println("BO na planilha");
                e.printStackTrace();
                
            }
    } catch (Exception e){

    }
}}
