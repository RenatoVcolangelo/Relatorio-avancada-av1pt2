package io.sim;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária responsável por **toda** a geração/atualização de relatórios
 * em Excel (Apache POI) para dados de:
 * <ul>
 *   <li>Telemetria dos veículos (RelatorioAuto.xlsx)</li>
 *   <li>Transações financeiras (RelatorioTransacao.xlsx)</li>
 *   <li>Reconciliação de dados de tempo&nbsp;e&nbsp;distância (Recon.xlsx)</li>
 * </ul>
 * A partir desta versão a classe passa a incorporar a {@link Estatistica} para
 * calcular <em>dinamicamente</em> os parâmetros estatísticos (média, desvio‑padrão,
 * polarização, precisão e incerteza) exigidos no item AV2.1 do enunciado. Esses
 * valores são adicionados em colunas extras, permitindo acompanhar a qualidade
 * das medições a cada iteração.
 */
public class Relatorio {
    /*------------------------------------------------------------------
     *  Arquivos de saída
     *----------------------------------------------------------------*/
    private static final String fileNameAuto       = "RelatorioAuto.xlsx";
    private static final String fileNameTransacao  = "RelatorioTransacao.xlsx";
    private static final String fileNameRec       = "Recon.xlsx";

    /*------------------------------------------------------------------
     *  1. Relatório de Telemetria dos Veículos
     *----------------------------------------------------------------*/
    public static synchronized void criaExcelAuto() {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet();
            Row cab = sh.createRow(0);
            String[] cols = {"TimeStamp","CarID","RoadID","RouteID","Speed","Odometro",
                             "FuelConsumption","FuelType","CO2Emission","Latitude",
                             "Longitude","Distance","EdgeDistance"};
            for (int i = 0; i < cols.length; i++) cab.createCell(i).setCellValue(cols[i]);
            try (FileOutputStream out = new FileOutputStream(fileNameAuto)) {
                wb.write(out);
            }
            System.out.println("Planilha " + fileNameAuto + " criada com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void manipulaExcelAuto(DrivingData d) {
        try (FileInputStream in = new FileInputStream(fileNameAuto);
             Workbook wb       = WorkbookFactory.create(in);
             FileOutputStream out = new FileOutputStream(fileNameAuto)) {

            Sheet sh = wb.getSheetAt(0);
            int last = sh.getLastRowNum() + 1;
            Row r = sh.createRow(last);
            r.createCell(0).setCellValue(d.getTimeStamp());
            r.createCell(1).setCellValue(d.getAutoID());
            r.createCell(2).setCellValue(d.getRoadIDSUMO());
            r.createCell(3).setCellValue(d.getRouteIDSUMO());
            r.createCell(4).setCellValue(d.getSpeed());
            r.createCell(5).setCellValue(d.getOdometer());
            r.createCell(6).setCellValue(d.getFuelConsumption());
            r.createCell(7).setCellValue(d.getFuelType());
            r.createCell(8).setCellValue(d.getCo2Emission());
            r.createCell(9).setCellValue(d.getLatitude());
            r.createCell(10).setCellValue(d.getLongitude());
            r.createCell(11).setCellValue(d.getDistance());
            r.createCell(12).setCellValue(d.getEdgeDist());
            wb.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*------------------------------------------------------------------
     *  2. Relatório de Transações Financeiras
     *----------------------------------------------------------------*/
    public static synchronized void criaExcelTransacao() {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet();
            Row cab = sh.createRow(0);
            String[] cols = {"TimeStamp","ContaPagadora","ContaRecebedora","Valor"};
            for (int i = 0; i < cols.length; i++) cab.createCell(i).setCellValue(cols[i]);
            try (FileOutputStream out = new FileOutputStream(fileNameTransacao)) {
                wb.write(out);
            }
            System.out.println("Planilha " + fileNameTransacao + " criada com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void manipulaExcelTransacao(Transacao t) {
        try (FileInputStream in = new FileInputStream(fileNameTransacao);
             Workbook wb       = WorkbookFactory.create(in);
             FileOutputStream out = new FileOutputStream(fileNameTransacao)) {

            Sheet sh = wb.getSheetAt(0);
            int last = sh.getLastRowNum() + 1;
            Row r = sh.createRow(last);
            r.createCell(0).setCellValue(t.getTimestamp());
            r.createCell(1).setCellValue(t.getContaPagador());
            r.createCell(2).setCellValue(t.getContaRecebedor());
            r.createCell(3).setCellValue(t.getValor());
            wb.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*------------------------------------------------------------------
     *  3. Relatório de Reconciliação (tempos/distâncias + Estatística)
     *----------------------------------------------------------------*/
    public static synchronized void criaExcelReconciliacao() {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet();
            Row cab = sh.createRow(0);
            String[] cols = {"Iter","t1","t2","t3","t4","t5","tT","d1","d2","d3","d4","d5","dT",
                             "MediaT","DesvT","BiasT","PrecisaoT","IncertezaT",
                             "MediaD","DesvD","BiasD","PrecisaoD","IncertezaD"};
            for (int i = 0; i < cols.length; i++) cab.createCell(i).setCellValue(cols[i]);
            try (FileOutputStream out = new FileOutputStream(fileNameRec)) {
                wb.write(out);
            }
            System.out.println("Planilha " + fileNameRec + " criada com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param tempos Lista com os tempos parciais + tempo total (tT) – tamanho 6.
     * @param dists  Lista com as distâncias parciais + distância total (dT) – tamanho 6.
     */
    public static synchronized void manipulaExcelRec(List<Double> tempos, List<Double> dists) {
        if (tempos.size() != 6 || dists.size() != 6) {
            System.err.println("ERRO: listas devem conter 6 valores (5 parciais + total). Dados ignorados.");
            return;
        }
        try (FileInputStream in = new FileInputStream(fileNameRec);
             Workbook wb       = WorkbookFactory.create(in);
             FileOutputStream out = new FileOutputStream(fileNameRec)) {

            Sheet sh = wb.getSheetAt(0);
            int last = sh.getLastRowNum() + 1;
            Row r = sh.createRow(last);
            r.createCell(0).setCellValue(last);           // Iteração

            // ---- grava tempos (t1..t5, tT) ----
            for (int i = 0; i < 6; i++) r.createCell(1 + i).setCellValue(tempos.get(i));
            // ---- grava distâncias (d1..d5, dT) ----
            for (int i = 0; i < 6; i++) r.createCell(7 + i).setCellValue(dists.get(i));

            /*-------------------------------------------
             * Estatística dos tempos
             *------------------------------------------*/
            double mediaT      = Estatistica.media(tempos);
            double desvioT     = Estatistica.desvioPadrao(tempos);
            double biasT       = Estatistica.polarizacao(tempos, tempos.getLast());  // usando tT como ref.
            double precisaoT   = Estatistica.precisao(tempos);
            double incertezaT  = Estatistica.incerteza(tempos);

            /*-------------------------------------------
             * Estatística das distâncias
             *------------------------------------------*/
            double mediaD      = Estatistica.media(dists);
            double desvioD     = Estatistica.desvioPadrao(dists);
            double biasD       = Estatistica.polarizacao(dists, dists.getLast());    // dT referência
            double precisaoD   = Estatistica.precisao(dists);
            double incertezaD  = Estatistica.incerteza(dists);

            int c = 13; // coluna inicial para estatísticas de tempo
            r.createCell(c++).setCellValue(mediaT);
            r.createCell(c++).setCellValue(desvioT);
            r.createCell(c++).setCellValue(biasT);
            r.createCell(c++).setCellValue(precisaoT);
            r.createCell(c++).setCellValue(incertezaT);
            // Estatísticas de distância
            r.createCell(c++).setCellValue(mediaD);
            r.createCell(c++).setCellValue(desvioD);
            r.createCell(c++).setCellValue(biasD);
            r.createCell(c++).setCellValue(precisaoD);
            r.createCell(c).setCellValue(incertezaD);

            wb.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}