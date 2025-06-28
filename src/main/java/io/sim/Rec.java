package io.sim;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Rec {

	public static void main(String[] args) {
		try {
			// Abrir a planilha Recon.xlsx
			FileInputStream file = new FileInputStream("Recon.xlsx");
			Workbook workbook = new XSSFWorkbook(file);
			Sheet sheet = workbook.getSheetAt(0);

			// Pegar a última linha com dados (valores de tempo)
			int lastRow = sheet.getLastRowNum();
			Row row = sheet.getRow(lastRow);

			ArrayList<Double> tempos = new ArrayList<>();
			for (int i = 1; i <= 6; i++) {
				Cell cell = row.getCell(i);
				tempos.add(cell.getNumericCellValue());
			}

			// Converter para array de double
			double[] y = tempos.stream().mapToDouble(Double::doubleValue).toArray();

			// Calcular média
			double media = 0;
			for (double t : y) {
				media += t;
			}
			media /= y.length;

			// Calcular desvio padrão (dinâmico)
			double[] v = new double[y.length];
			for (int i = 0; i < y.length; i++) {
				v[i] = Math.sqrt(Math.pow(y[i] - media, 2));
			}

			// Matriz de incidência: somatório dos fluxos = fluxo total
			double[][] A = new double[][] { { -1, -1, -1, -1, -1, 1 } };

			// Executar reconciliação
			Reconciliation rec = new Reconciliation(y, v, A);
			double[] y_hat = rec.getReconciledFlow();

			System.out.println("Y_hat:");
			rec.printMatrix(y_hat);

			// Converter resultado reconciliado para listas
			List<Double> temposReconciliados = new ArrayList<>();
			for (double d : y_hat) temposReconciliados.add(d);

			// Neste exemplo, usaremos os mesmos valores reconciliados como distância fictícia
			//Relatorio.manipulaExcelRecReconciliado(temposReconciliados, temposReconciliados);

			workbook.close();
			file.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
