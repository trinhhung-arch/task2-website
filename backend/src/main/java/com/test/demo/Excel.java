package com.test.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

// Builds a one-sheet .xlsx file (bold header row + data rows) for the "Export Excel" buttons
final class Excel {

	static final MediaType XLSX = MediaType
			.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

	private Excel() {
	}

	static ResponseEntity<byte[]> download(String fileName, String sheetName, List<String> headers,
			List<List<String>> rows) {
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(fileName).build().toString())
				.contentType(XLSX)
				.body(workbook(sheetName, headers, rows));
	}

	static byte[] workbook(String sheetName, List<String> headers, List<List<String>> rows) {
		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet(sheetName);
			Font boldFont = workbook.createFont();
			boldFont.setBold(true);
			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFont(boldFont);

			writeRow(sheet.createRow(0), headers, headerStyle);
			for (int i = 0; i < rows.size(); i++) {
				writeRow(sheet.createRow(i + 1), rows.get(i), null);
			}
			for (int column = 0; column < headers.size(); column++) {
				sheet.autoSizeColumn(column);
			}
			workbook.write(out);
			return out.toByteArray();
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	// Values are written as text cells, so a task named "=1+1" is never run as a formula
	private static void writeRow(Row row, List<String> values, CellStyle style) {
		for (int i = 0; i < values.size(); i++) {
			var cell = row.createCell(i);
			cell.setCellValue(values.get(i) == null ? "" : values.get(i));
			if (style != null) {
				cell.setCellStyle(style);
			}
		}
	}
}
