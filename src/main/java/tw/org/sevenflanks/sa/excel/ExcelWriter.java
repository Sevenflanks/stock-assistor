package tw.org.sevenflanks.sa.excel;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.ShapeTypes;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.poi.xssf.usermodel.XSSFTextBox;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import tw.org.sevenflanks.sa.excel.base.ExcelColumn;


public class ExcelWriter implements Closeable {

	private Workbook workBook;
	private Sheet currentSheet;
	private Row currentRow;
	private int currentRowNo = 0;
	private int startColumn = 0;
	//BORDER_HAIR(最細的虛線)實線可用BORDER_THIN
	private static short BORDER = HSSFCellStyle.BORDER_THIN;
	
	public Workbook getWorkbook() {
		Objects.requireNonNull(workBook, "workBook must not be null!!");
		return workBook;
	}
	
	/**
	 * 設定指定欄寬度
	 * @param columnNum
	 * 指定欄位(從0開始算)
	 * @param width
	 * 寬度
	 * @return
	 */
	public ExcelWriter setColumnWidth(int columnNum, int width) {
		this.currentSheet.setColumnWidth(columnNum, width);
		return this;
	}
	
	/** 
	 * 單純於某一格欄位上劃斜線 
	 * @param col
	 * @param row
	 */
	public ExcelWriter drawLine(int col, int row) {
		return drawLine(col, row, col, row);
	}
	
	/**
	 * 於指定欄位上劃斜線
	 * @param col1
	 * @param row1
	 * @param col2
	 * @param row2
	 * @return
	 */
	public ExcelWriter drawLine(int col1, int row1, int col2, int row2) {
		Drawing drawing = this.currentSheet.createDrawingPatriarch();
		XSSFClientAnchor anchor = getAnchor(col1, row1, col2, row2);
		
		XSSFSimpleShape shape = ((XSSFDrawing)drawing).createSimpleShape(anchor);
		shape.setShapeType(ShapeTypes.LINE);
		shape.setLineWidth(0.5);
		shape.setLineStyle(0);
		shape.setLineStyleColor(0, 0, 0);
		
		return this;
	}
	
	/**
	 * 於指定欄位上加上文字方塊
	 * @param col1
	 * @param row1
	 * @param col2
	 * @param row2
	 * @param value
	 * @return
	 */
	public ExcelWriter addTextBox(int col1, int row1, int col2, int row2, String value) {
		Objects.requireNonNull(value, "Text can't be null when add TextBox!!");
		Drawing drawing = this.currentSheet.createDrawingPatriarch();
		XSSFClientAnchor anchor = getAnchor(col1, row1, col2, row2);
		
		XSSFTextBox createTextbox = ((XSSFDrawing)drawing).createTextbox(anchor);
		// 進一步設定字體、字型...
		XSSFRichTextString xssfRichTextString = new XSSFRichTextString(value);
		xssfRichTextString.applyFont(getFont());
		createTextbox.setText(xssfRichTextString);
		return this;
	}

	/**
	 * 取得固定影像(shap)的錨點，因錨點會從指定欄位的起點開始畫(x:0, y:0)
	 * <br>如果要在第一行第一列的一個欄位畫上斜線，不能兩個欄位參數都給同一個欄位(ex: 1, 1, 1, 1)
	 * <br>要給1, 1, 2, 2，但由於這樣會使複雜化使用，故此由ExcelWriter內部做
	 * @param col1
	 * @param row1
	 * @param col2
	 * @param row2
	 * @return
	 */
	private XSSFClientAnchor getAnchor(int col1, int row1, int col2, int row2) {
		CreationHelper helper = this.workBook.getCreationHelper();
		ClientAnchor anchor = helper.createClientAnchor();
		anchor.setCol1(col1);
		anchor.setRow1(row1); 
		anchor.setCol2(col2 + 1);
		anchor.setRow2(row2 + 1);
		return (XSSFClientAnchor)anchor;
	}
	
	/**
	 * 設定預設列高度(建議在建立Sheet後就設定)
	 * @param d
	 * @return
	 */
	public ExcelWriter setDefaultRowHeight(short d) {
		this.currentSheet.setDefaultRowHeight((short) d);
		return this;
	}
	
	/**
	 * 設定預設欄寬度(建議在建立Sheet後就設定)
	 * @param width
	 * 寬度
	 * @return
	 */
	public ExcelWriter setDefaultColumnWidth(int width) {
		this.currentSheet.setDefaultColumnWidth(width);
		return this;
	}
	
	/** 
	 * <br>自行指定目前列數(以poi的角度，從0開始)
	 * <br>需自行處理誤差
	 * <br>p.s.可能會有指定的row不存在的情況
	 * @param currentRowNo
	 * 	列位置
	 **/
	public ExcelWriter setCurrentRow(int currentRowNo) {
		Row row = currentSheet.getRow(currentRowNo);
		this.currentRowNo = currentRowNo;
		this.currentRow = row;
		if (row == null) {
			this.nextRow();
		}
		return this;
	}
	
	/**
	 * 取得目前的Sheet
	 * @return
	 */
	public Sheet getCurrentSheet() {
		return currentSheet;
	}
	
	/** 以Xlsx格式初始化ExcelWriter */
	public ExcelWriter() {
		// 一般情況下使用XSSFWorkbook即可
		// 但若為大量資料寫入的情況, 則建議使用SXSSFWorkbook
		// SXSSFWorkbook將會限制一次可操作的row範圍, 優化系統效能的負擔
		this.workBook = new XSSFWorkbook();
		
	}

	/**
	 * 以自訂格式初始化ExcelWriter
	 * Xls: new HSSFWorkbook
	 * Xlsx: new SXSSFWorkbook(大量資料寫入建議使用) or new XSSFWorkbook
	 */
	public ExcelWriter(Workbook workBook) {
		this.workBook = workBook;
	}

	/** 設定起始行(從0開始) */
	public ExcelWriter setStartRow(int i) {
		currentRowNo = i;
		return this;
	}

	/** 設定起始欄(從0開始) */
	public ExcelWriter setStartColumn(int i) {
		startColumn = i;
		return this;
	}

	/** 設定起始行(從0開始) */
	public int getCurrentRowNo() {
		return currentRowNo;
	}
	
	/** 取得目前行 */
	public Row getCurrentRow() {
		
		return currentRow;
	}

	/** 設定起始欄(從0開始) */
	public int getCurrentColumnNo(int i) {
		return startColumn;
	}

	/** 建立欄位樣式 */
	public CellStyle createCellStyle() {
		CellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setFont(getFont());
		return cellStyle;
	}

	/** 建立欄位格式 */
	public DataFormat createDataFormat() {
		return workBook.createDataFormat();
	}

	/** 建立格式的欄位樣式 */
	public CellStyle createFormatStyle(String pattern) {
		final CellStyle cellStyle = this.createCellStyle();
		final DataFormat format = this.createDataFormat();
		cellStyle.setDataFormat(format.getFormat(pattern));
		return cellStyle;
	}

	/**
	 * 建立新的工作表
	 * 若工作表名稱已經存在, 則會使用既有的工作表
	 * @param sheetName
	 * @return
	 */
	public ExcelWriter createSheet(String sheetName) {
		final Sheet sheet = workBook.getSheet(sheetName);
		if (sheet != null) {
			currentSheet = sheet;
		} else {
			currentSheet = workBook.createSheet(sheetName);
		}
		currentRowNo = 0;
		startColumn = 0;
		return this;
	}
	
	/**
	 * 若有事先import template，且要針對import的Sheet(根據index)做後續加工，要先呼叫此Method
	 * @param sheetName
	 * @return
	 */
	public ExcelWriter setFirstSheetName(String sheetName) {
		return setSheetNameByIndex(0, sheetName);
	}
	
	/**
	 * 若有事先import template，且要針對import的Sheet(根據index)做後續加工，要先呼叫此Method
	 * @param sheetName
	 * @return
	 */
	public ExcelWriter setSheetNameByIndex(int indexOf, String sheetName) {
		
		try {
			currentSheet = workBook.getSheetAt(indexOf);
			workBook.setSheetName(indexOf, sheetName);
			currentRowNo = 0;
			startColumn = 0;
		} catch (Exception ex) {
			return this.createSheet(String.valueOf(sheetName));
		}
		
		return this;
	}
	
	public ExcelWriter addRows(int rows) { 
		IntStream.of(0, rows).forEach(i -> this.nextRow());
		return this;
	}
	
	/**
	 * <br>下一個已存在列，若不存在則建立
	 * <br>預防在已有資料的列上建立新的列，會將資料洗掉
	 * @return
	 */
	public ExcelWriter nextRow() {
		Objects.requireNonNull(currentSheet, "必須至少建立一個Sheet");
		currentRow = currentSheet.getRow(currentRowNo + 1);
		if (currentRow == null) {
			currentRow = currentSheet.createRow(currentRowNo);
			currentRowNo++;
		} else {
			currentRowNo++;	//若該列存在才須
		}
		return this;
	}
	
	public ExcelWriter addTitleCell(int columnNo, Object value) {
		CellStyle cellStyle = workBook.createCellStyle();
		Font font = getFont();
		font.setBold(true);
		cellStyle.setFont(font);
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		return addCell(columnNo, value, cellStyle);
	}
	
	private Font getFont() {
		Font font = workBook.createFont();
		font.setFontName("標楷體");
		return font;
	}
	
	/**
	 * 建立新的欄
	 * @param columnNo
	 * 		欄位編號(從0開始)
	 * @param value
	 * 		欄位值
	 * @return
	 */
	public ExcelWriter addCell(int columnNo, Object value) {
		Objects.requireNonNull(currentRow, "必須至少建立一個Row");
		Cell cell = currentRow.createCell(columnNo);
		cell.setCellStyle(createCellStyle());
		setValueToCell(cell, value);
		return this;
	}

	/**
	 * 建立新的欄
	 * @param columnNo
	 * 		欄位編號(從0開始)
	 * @param value
	 * 		欄位值
	 * @param style
	 * 		欄位樣式
	 * @return
	 */
	public ExcelWriter addCell(int columnNo, Object value, CellStyle style) {
		Objects.requireNonNull(currentRow, "必須至少建立一個Row");
		final Cell cell = currentRow.createCell(columnNo);
		cell.setCellStyle(style);
		setValueToCell(cell, value);
		return this;
	}
	
	/**
	 * 建立新的有框線欄
	 * @param columnNo
	 * 		欄位編號(從0開始)
	 * @param value
	 * 		欄位值
	 * @return
	 */
	public ExcelWriter addBorderCell(int columnNo, Object value) {
		return addCell(columnNo, value, getBorderStyle());
	}
	
	/**
	 * 建立新的有框線欄
	 * @param columnNo
	 * 		欄位編號(從0開始)
	 * @param value
	 * 		欄位值
	 * @param style
	 * 		欄位樣式
	 * @return
	 */
	public ExcelWriter addBorderCell(int columnNo, Object value, CellStyle style) {
		return addCell(columnNo, value, getBorderStyle(style));
	}
	
	/**
	 * 建立新的有框線欄，並為百分比格式
	 * @param columnNo
	 * 		欄位編號(從0開始)
	 * @param value
	 * 		欄位值
	 * @return
	 */
	public ExcelWriter addBorderAndRateCell(int columnNo, Object value) {
		return addCell(columnNo, value, getBorderStyle(getPercentageFormatStyle()));
	}
	
	/**
	 * 建立新的有框線欄
	 * @param columnNo
	 * 		欄位編號(從0開始)
	 * @param value
	 * 		欄位值
	 * @return
	 */
	public ExcelWriter addBorderAndCenterCell(int columnNo, Object value) {
		return addCell(columnNo, value, getBorderStyle(getAlignCenter()));
	}

	/**
	 * 建立新的有框線欄
	 * @param columnNo
	 * 		欄位編號(從0開始)
	 * @param value
	 * 		欄位值
	 * @param style
	 * 		欄位樣式
	 * @return
	 */
	public ExcelWriter addBorderAndCenterCell(int columnNo, Object value, CellStyle style) {
		return addCell(columnNo, value, getBorderStyle(getAlignCenter(style)));
	}
	
	/**
	 * 建立新的欄(公式)
	 * @param columnNo
	 * 		欄位編號(從0開始)
	 * @param formula
	 * 		欄位值
	 * @return
	 */
	public ExcelWriter addCellFormula(int columnNo, String formula) {
		Objects.requireNonNull(currentRow, "必須至少建立一個Row");
		currentRow.createCell(columnNo).setCellFormula(formula);
		return this;
	}

	/**
	 * 建立新的欄(公式)
	 * @param columnNo
	 * 		欄位編號(從0開始)
	 * @param formula
	 * 		欄位值
	 * @param style
	 * 		欄位樣式
	 * @return
	 */
	public ExcelWriter addCellFormula(int columnNo, String formula, CellStyle style) {
		Objects.requireNonNull(currentRow, "必須至少建立一個Row");
		final Cell cell = currentRow.createCell(columnNo);
		cell.setCellFormula(formula);
		cell.setCellStyle(style);
		return this;
	}
	
	/**
	 * 建立新的欄(公式)
	 * @param columnNo
	 * 		欄位編號(從0開始)
	 * @param formula
	 * 		欄位值
	 * @return
	 */
	public ExcelWriter addBorderCellFormula(int columnNo, String formula) {
		return addCellFormula(columnNo, formula, getBorderStyle());
	}

	/**
	 * <br>建立新的有框線欄
	 * <br>並轉換為百分比格式
	 * @param columnNo
	 * 		欄位編號(從0開始)
	 * @param value
	 * 		欄位值
	 * @return
	 */
	public ExcelWriter addBorderRateCellFormula(int columnNo, String formula) {
		return addCellFormula(columnNo, formula, getBorderStyle(getPercentageFormatStyle()));
	}
	
	/**
	 * 合併儲存格
	 * @param firstRow
	 * 		起始行
	 * @param lastRow
	 * 		結束行
	 * @param firstCol
	 * 		起始欄
	 * @param lastCol
	 * 		結束欄
	 * @return
	 */
	public ExcelWriter mergedRegion(int firstRow, int lastRow, int firstCol, int lastCol) {
		Objects.requireNonNull(currentSheet, "必須至少建立一個Sheet");
		currentSheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
		return this;
	}
	
	/**
	 * 合併本行儲存格
	 * @param firstCol
	 * 		起始欄
	 * @param lastCol
	 * 		結束欄
	 * @return
	 */
	public ExcelWriter mergedPresentRegion(int firstCol, int lastCol) {
		Objects.requireNonNull(currentSheet, "必須至少建立一個Sheet");
		return mergedRegionWithBorder(currentRowNo, currentRowNo, firstCol, lastCol);
	}
	
	/**
	 * 合併儲存格，並賦予框線
	 * @param firstRow
	 * 		起始行
	 * @param lastRow
	 * 		結束行
	 * @param firstCol
	 * 		起始欄
	 * @param lastCol
	 * 		結束欄
	 * @return
	 */
	public ExcelWriter mergedRegionWithBorder(int firstRow, int lastRow, int firstCol, int lastCol) {
		Objects.requireNonNull(currentSheet, "必須至少建立一個Sheet");
		CellRangeAddress range = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
		currentSheet.addMergedRegion(range);
		RegionUtil.setBorderBottom(BORDER, range, this.currentSheet, this.workBook);
		RegionUtil.setBorderTop(BORDER, range, this.currentSheet, this.workBook);
		RegionUtil.setBorderLeft(BORDER, range, this.currentSheet, this.workBook);
		RegionUtil.setBorderRight(BORDER, range, this.currentSheet, this.workBook);
		//若有需要可客製框線顏色RegionUtil.setBottomBorderColor
		return this;
	}

	/**
	 * 建立Grid
	 * @param columns 欄位定義檔
	 * @param datas 明細檔案List
	 */
	public ExcelWriter grid(List<ExcelColumn<?, ?>> columns, List<?> datas) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Objects.requireNonNull(currentSheet, "必須至少建立一個Sheet");

		int rowNo = currentRowNo;
		// titles
		if (columns.stream().anyMatch(c -> c.getTitle() != null)) {
			final Row row = currentSheet.createRow(rowNo++);
			int columnNo = startColumn;
			for (final ExcelColumn<?, ?> column : columns) {
				row.createCell(columnNo).setCellValue(column.getTitle());
				row.getCell(columnNo).setCellStyle(column.getTitleStyle());
				if (column.getWidth() != null && column.getWidth() > 0) {
					currentSheet.setColumnWidth(columnNo, column.getWidth() * 256);
				} else {
					currentSheet.autoSizeColumn(columnNo);
				}
				columnNo++;
			}
		}
		// datas
		if (!datas.isEmpty()) {
			handleGetters(columns, datas);

			for (final Object t :datas) {
				final Row row = currentSheet.createRow(rowNo++);
				int columnNo = startColumn;
				for (final ExcelColumn<?, ?> c : columns) {
					final Cell cell = row.createCell(columnNo++);

					if (c.getStyle() != null) {
						cell.setCellStyle(c.getStyle());
					}
					
					if (c.getField() != null && !Objects.equals(c.getField(), "")) {
						// 若有設定field, 根據field指定的欄位名稱取得資料
						final Object value = c.getMethod().invoke(t);
						if (value != null) {
							setValueToCell(cell, c.formate(value));
						} else {
							cell.setCellValue("");
						}
					} else {
						// 若沒有設定field, 則直接使用formate取得本欄位的資料
						setValueToCell(cell, c.formate(t));
					}
					
				}
			}
		}
		// 處理自動寬度
		int columnNo = startColumn;
		for (final ExcelColumn<?, ?> column : columns) {
			if (column.getWidth() == null || column.getWidth() == 0) {
				currentSheet.autoSizeColumn(columnNo);
			}
			columnNo++;
		}

		currentRowNo = rowNo;
		nextRow();
		return this;
	}

	/**
	 * 橫向合併, 處理每一行
	 * @return
	 */
	public ExcelWriter autoGroupingRow() {
		for (final Row row : currentSheet) {
			autoGroupingRow(row);
		}
		return this;
	}

	/**
	 * 橫向合併, 處理特定行
	 * @param rowNo 第幾列要處理合併
	 * @return
	 */
	public ExcelWriter autoGroupingRow(int rowNo) {
		final Row row = currentSheet.getRow(rowNo);
		return autoGroupingRow(row);
	}

	private ExcelWriter autoGroupingRow(Row row) {
		int firstCol = -1;
		int lastCol = -1;
		Cell cacheCell = null;
		for (final Cell cell : row) {
			if (cacheCell != null && cellToString(cacheCell).equals(cellToString(cell))) {
				// 值等同於之前記憶的colume, 繼續合併
				lastCol = cell.getColumnIndex();
			} else {
				// 值不等同於之前記憶的colume, 處理合併(只有在firstCol與lastCol不同時處理)
				if (firstCol != lastCol) {
					currentSheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), firstCol, lastCol));
				}
				// 開始一個新的合併
				cacheCell = cell;
				firstCol = cell.getColumnIndex();
				lastCol = cell.getColumnIndex();
			}
		}
		if (firstCol != lastCol) {
			currentSheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), firstCol, lastCol));
		}
		return this;
	}

	/**
	 * 縱向合併, 處理每一列
	 * @return
	 */
	public ExcelWriter autoGroupingCol() {
		final Optional<Short> maxColNo = StreamSupport.stream(currentSheet.spliterator(), false).map(Row::getLastCellNum).max(Comparator.naturalOrder());
		if (maxColNo.isPresent()) {
			for (int i = 0; i <= maxColNo.get(); i++) {
				autoGroupingCol(i);
			}
		}
		return this;
	}

	/**
	 * 縱向合併, 處理特定行
	 * @param colNo 第幾列要處理合併
	 * @return
	 */
	public ExcelWriter autoGroupingCol(Integer colNo) {
		int firstRow = -1;
		int lastRow = -1;
		Cell cacheCell = null;
		for (final Row row : currentSheet) {
			final Cell cell = row.getCell(colNo);
			if (cell == null) {
				// 該格可能不存在, 先嘗試合併之前記憶的儲存格, 接下來當作合併從沒有開始
				if (firstRow != lastRow) {
					currentSheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, colNo, colNo));
				}
				cacheCell = null;
				firstRow = -1;
				lastRow = -1;
			} else if (cacheCell != null && cellToString(cacheCell).equals(cellToString(cell))) {
				// 值等同於之前記憶的colume, 繼續合併
				lastRow = row.getRowNum();
			} else {
				// 值不等同於之前記憶的colume, 處理合併(只有在firstCol與lastCol不同時處理)
				if (firstRow != lastRow) {
					currentSheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, colNo, colNo));
				}
				// 開始一個新的合併
				cacheCell = cell;
				firstRow = row.getRowNum();
				lastRow = row.getRowNum();
			}
		}
		if (firstRow != lastRow) {
			currentSheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, colNo, colNo));
		}
		return this;
	}

	/**
	 * 根據value的型別分別塞到cell裡面
	 * 目前會有特殊轉型的型別為Boolean, Calendar, Date, Double, LocalDate, LocalDateTime
	 * 若不屬於上述型別, 則一律呼叫toString方法
	 * @param cell
	 * @param value
	 */
	private void setValueToCell(Cell cell, Object value) {
		if (value == null) {
			cell.setCellValue("");
		} else if (value instanceof Long) {
			//指定欄位格式為數字型態才可做Formula 運算
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue((Long) value);
		} else if (value instanceof Integer) {
			//指定欄位格式為數字型態才可做Formula 運算
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
			cell.setCellValue((Boolean) value);
		} else if (value instanceof Calendar) {
			cell.setCellValue((Calendar) value);
		} else if (value instanceof Date) {
			cell.setCellValue((Date) value);
		} else if (value instanceof Double) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue((Double) value);
		} else if (value instanceof LocalDate) {
			cell.setCellValue(Date.from(((LocalDate)value).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		} else if (value instanceof LocalDateTime) {
			cell.setCellValue(Date.from(((LocalDateTime)value).toInstant(ZoneOffset.UTC)));
		} else {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(value.toString());
		}
	}

	/**
	 * 處理getter的method
	 * 這方法是為了避免每行資料都需反射getter而設計的
	 * @param columns
	 * @param datas
	 */
	private void handleGetters(List<ExcelColumn<?, ?>> columns, List<?> datas) {
		try {
			final PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(datas.get(0).getClass()).getPropertyDescriptors();
			columns.forEach(c -> {
				final Method method = Stream.of(propertyDescriptors)
						.filter(p -> p.getName().equals(c.getField()))
						.findFirst()
						.map(PropertyDescriptor::getReadMethod)
						.orElse(null);
				c.setMethod(method);
			});
		} catch (final IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 輸出
	 * @param outputStream
	 * @throws IOException
	 */
	public void write(OutputStream outputStream) throws IOException {
		try {
			workBook.write(outputStream);
		} finally {
			workBook.close();
		}
	}

	@Override
	public void close() throws IOException {
		workBook.close();
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
				// 若為布林則轉成文字
				result = Boolean.toString(cell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				// 若為數字則轉成文字
				result = Double.toString(cell.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_FORMULA:
				// 若為公式, 則必須一直唯一
				result = Integer.toString(cell.hashCode());
				break;
			default:
				// 其餘類型則當作String塞
				result = cell.getStringCellValue();
				break;
		}
		return result;
	}
	
	/**
	 * 產生百分比格式
	 * @return
	 */
	private CellStyle getPercentageFormatStyle() {
		CellStyle style = this.createCellStyle();
		style.setDataFormat(this.createDataFormat().getFormat("0.00%"));		
		return style;
	}
	
	private CellStyle getBorderStyle() {
		final CellStyle style = this.createCellStyle();
		style.setBorderBottom(BORDER);
		style.setBorderTop(BORDER);
		style.setBorderRight(BORDER);
		style.setBorderLeft(BORDER);
		style.setWrapText(true);
		return style;
	}
	
	private CellStyle getBorderStyle(final CellStyle style) {
		style.setBorderBottom(BORDER);
		style.setBorderTop(BORDER);
		style.setBorderRight(BORDER);
		style.setBorderLeft(BORDER);
		style.setWrapText(true);
		return style;
	}
	
	private CellStyle getAlignCenter() {
		final CellStyle style = this.createCellStyle();
		style.setWrapText(true);
	    style.setAlignment(CellStyle.ALIGN_CENTER);
	    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		return style;
	}
	
	private CellStyle getAlignCenter(final CellStyle style) {
		style.setWrapText(true);
	    style.setAlignment(CellStyle.ALIGN_CENTER);
	    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		return style;
	}

	public ExcelWriter insertRow(int rowNo, int count) {
		currentSheet.shiftRows(rowNo, currentSheet.getLastRowNum(), count);
		return this;
	}

}
