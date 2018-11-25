package tw.org.sevenflanks.sa.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ExcelReader implements Closeable {
	protected Logger log = LoggerFactory.getLogger(getClass());

	private String fileName;
	private String fileExtension;

	private InputStream fis;
	private Workbook workBook;

	/**
	 * ExcelReader
	 * 專門讀取Excel所使用
	 * 若讀取的路徑不存在、不為Excel則會拋錯
	 * 
	 * 支援2007以前與以後的版本(xls,xlsx)
	 * @throws InvalidFormatException 
	 */
	public ExcelReader(File file) throws IOException, InvalidFormatException {
		if (!file.exists()) {
			throw new RuntimeException("File not exist, FilePath:{}" + file.getAbsolutePath());
		}
		
		final int lastIndexOfDot = file.getName().lastIndexOf('.');
		fileName = file.getName().substring(0, lastIndexOfDot);
		fileExtension = file.getName().substring(lastIndexOfDot + 1);
		
		fis = new FileInputStream(file);
		log.info("Readed File {}.{}", fileName, fileExtension);
		
		workBook = WorkbookFactory.create(file);
	}

	public ExcelReader(InputStream input) throws IOException, InvalidFormatException {
		fis = input;
		workBook = WorkbookFactory.create(input);
	}
	
	/**
	 * 讀取第一個Sheet
	 * 此方法針對有標題的表單, 若不是此格式將會拋錯
	 * */
	public List<Map<String, String>> readSheet() {
		return sheetToListMap(workBook.getSheetAt(0));
	}
	
	/**
	 * 讀取特定Sheet
	 * 此方法針對第一行為標題的表單, 若不是此格式將會拋錯
	 * */
	public List<Map<String, String>> readSheet(String sheetName) {
		return sheetToListMap(workBook.getSheet(sheetName));
	}
	
	/**
	 * 將Sheet轉成List<Map<String, String>>
	 * List中每一個單位是一比record
	 * Map的Key為Title, Value為
	 */
	private List<Map<String, String>> sheetToListMap(Sheet sheet) {
		List<Map<String, String>> result = new ArrayList<>();
		final Row titleRow = sheet.getRow(0);
		final Map<Integer, String> titles = rowToMap(titleRow);
		
		if (titles == null) {
			throw new RuntimeException("First row of sheet is require");
		}
		
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			final Row row = sheet.getRow(i);
			if (row != null) {
				final Map<String, String> rowMap = new HashMap<>();
				for (Entry<Integer, String> entry : titles.entrySet()) {
					final Cell cell = row.getCell(entry.getKey());
					final String cellValue = cellToString(cell);
					if (cellValue != null && !cellValue.isEmpty()) {
						rowMap.put(entry.getValue(), cellValue);
					}
				}
				result.add(rowMap);
			}
		}
		
		return result;
	}
	
	/**
	 * 將Row轉成Map, Key為Cell位置, Value為標題名稱
	 * 只會將有值的欄位塞入
	 */
	private Map<Integer, String> rowToMap(final Row titleRow) {
		boolean isAllBlank = true;
		final Map<Integer, String> titles = new HashMap<>();
		for (int i = titleRow.getFirstCellNum(); i <= titleRow.getLastCellNum(); i++) {
			final Cell titleCell = titleRow.getCell(i);
			String title = cellToString(titleCell);
			if (title != null && !title.isEmpty()) {
				titles.put(i, title);
				isAllBlank = false;
			}
		}
		
		if (isAllBlank) {
			return null;
		} else {
			return titles;
		}
		
	}
	
	private String cellToString(Cell cell) {
		if (cell == null) {
			return null;
		}
		final String result;
		final int cellType = cell.getCellType();
		switch (cellType) {
			case Cell.CELL_TYPE_BLANK:
				// 若為空則略過
				result = null;
				break;
			case Cell.CELL_TYPE_ERROR:
				// 若有錯則塞ERROR
				result = "ERROR";
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				// 若為空則略過
				result = Boolean.toString(cell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				// 若為數字則塞文字
				result = Double.toString(cell.getNumericCellValue());
				break;
			default:
				// 其餘類型則當作String塞
				result = cell.getStringCellValue();
				break;
		}
		return result;
	}
	
	public Workbook getWorkBook() {
		return workBook;
	}

	@Override
	protected void finalize() throws Throwable {
		fis.close();
		super.finalize();
	}

	@Override
	public void close() throws IOException {
		fis.close();
	}

}
