package io.sim;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Relatorio {

    // Nomes dos arquivos para relatórios de automóveis e transações
    private static final String fileNameAuto = "RelatorioAuto.xlsx";
    private static final String fileNameTransacao = "RelatorioTransacao.xlsx";

    // Método para criar o arquivo Excel de relatório de automóveis
    public static synchronized void criaExcelAuto() {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            Row row = sheet.createRow(0);

            row.createCell(0).setCellValue("TimeStamp");
            row.createCell(1).setCellValue("CarID");
            row.createCell(2).setCellValue("RoadID");
            row.createCell(3).setCellValue("ROuteID");
            row.createCell(4).setCellValue("Speed");
            row.createCell(5).setCellValue("Odometro");
            row.createCell(6).setCellValue("Fuel Consuption");
            row.createCell(7).setCellValue("Fuel Type");
            row.createCell(8).setCellValue("CO2Emission");
            row.createCell(9).setCellValue("Latitude");
            row.createCell(10).setCellValue("Longitude");
            row.createCell(11).setCellValue("Distance");
            row.createCell(12).setCellValue("EdgeDistance");

            try (FileOutputStream outputStream = new FileOutputStream(fileNameAuto)) {
                workbook.write(outputStream);
                System.out.println("Planilha RelatorioAuto.xlsx criada com sucesso!");
                workbook.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para criar o arquivo Excel de relatório de transações
    public static synchronized void criaExcelTransacao() {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            Row row = sheet.createRow(0);

            row.createCell(0).setCellValue("TimeStamp");
            row.createCell(1).setCellValue("Conta pagadora");
            row.createCell(2).setCellValue("Conta recebedora");
            row.createCell(3).setCellValue("Valor");

            try (FileOutputStream outputStream = new FileOutputStream(fileNameTransacao)) {
                workbook.write(outputStream);
                System.out.println("Planilha RelatorioTransacao.xlsx criada com sucesso!");
                workbook.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para adicionar dados ao Excel de automóveis
    public static synchronized void manipulaExcelAuto(DrivingData r) {
        try (
            FileInputStream inputStream = new FileInputStream(fileNameAuto);
            Workbook workbook = WorkbookFactory.create(inputStream);
            FileOutputStream outputStream = new FileOutputStream(fileNameAuto)
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();
            Row row = sheet.createRow(lastRow + 1);

            row.createCell(0).setCellValue(r.getTimeStamp());
            row.createCell(1).setCellValue(r.getAutoID());
            row.createCell(2).setCellValue(r.getRoadIDSUMO());
            row.createCell(3).setCellValue(r.getRouteIDSUMO());
            row.createCell(4).setCellValue(r.getSpeed());
            row.createCell(5).setCellValue(r.getOdometer());
            row.createCell(6).setCellValue(r.getFuelConsumption());
            row.createCell(7).setCellValue(r.getFuelType());
            row.createCell(8).setCellValue(r.getCo2Emission());
            row.createCell(9).setCellValue(r.getLatitude());
            row.createCell(10).setCellValue(r.getLongitude());
            row.createCell(11).setCellValue(r.getDistance());
            row.createCell(12).setCellValue(r.getEdgeDist());

            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para adicionar dados ao Excel de transações
    public static synchronized void manipulaExcelTransacao(Transacao t) {
        try (
            FileInputStream inputStream = new FileInputStream(fileNameTransacao);
            Workbook workbook = WorkbookFactory.create(inputStream);
            FileOutputStream outputStream = new FileOutputStream(fileNameTransacao)
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();
            Row row = sheet.createRow(lastRow + 1);

            row.createCell(0).setCellValue(t.getTimestamp());
            row.createCell(1).setCellValue(t.getContaPagador());
            row.createCell(2).setCellValue(t.getContaRecebedor());
            row.createCell(3).setCellValue(t.getValor());

            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
