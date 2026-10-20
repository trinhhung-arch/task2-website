package com.test.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class ExcelTest {

	@Test
	void writesBoldHeaderAndTextRows() throws Exception {
		byte[] file = Excel.workbook("ToDo", List.of("Task", "Due date", "Status"),
				List.of(List.of("To do Task 3", "2026-10-20", "Pending"), List.of("=1+1", "", "Done")));

		try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(file))) {
			Sheet sheet = workbook.getSheet("ToDo");
			assertEquals(2, sheet.getLastRowNum());
			assertEquals("Task", sheet.getRow(0).getCell(0).getStringCellValue());
			assertTrue(workbook.getFontAt(sheet.getRow(0).getCell(0).getCellStyle().getFontIndex()).getBold());
			assertFalse(workbook.getFontAt(sheet.getRow(1).getCell(0).getCellStyle().getFontIndex()).getBold());
			assertEquals("2026-10-20", sheet.getRow(1).getCell(1).getStringCellValue());
			// a formula-looking title stays plain text
			assertEquals(CellType.STRING, sheet.getRow(2).getCell(0).getCellType());
			assertEquals("=1+1", sheet.getRow(2).getCell(0).getStringCellValue());
		}
	}
}
