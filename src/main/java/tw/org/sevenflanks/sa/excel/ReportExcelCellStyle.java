package tw.org.sevenflanks.sa.excel;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;

public class ReportExcelCellStyle {

	/** 欄位格式(置中) */
	public static CellStyle getCenterAlignStyle(final ExcelWriter ew) {
	    final CellStyle style = ew.createCellStyle();
	    style.setWrapText(true);
	    style.setAlignment(CellStyle.ALIGN_CENTER);
	    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    return style;
	}
	
	/** 欄位格式(置中) */
	public static CellStyle getCenterAlignStyleWithoutWrap(final ExcelWriter ew) {
	    final CellStyle style = ew.createCellStyle();
	    style.setWrapText(false);
	    style.setAlignment(CellStyle.ALIGN_CENTER);
	    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    return style;
	}
	
	/** 欄位格式(靠右) */
	public static CellStyle getRightAlignStyle(final ExcelWriter ew) {
	    final CellStyle style = ew.createCellStyle();
	    style.setWrapText(true);
	    style.setAlignment(CellStyle.ALIGN_RIGHT);
	    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    return style;
	}
	
	/** 欄位格式 + 邊框(置中) */
	public static CellStyle getCenterAlignBorderStyle(final ExcelWriter ew) {
		final CellStyle style = ew.createCellStyle();
		getCenterAlignBorderStyle(style);
	    return style;
	}
	
	/** 欄位格式 + 邊框(置中) */
	public static CellStyle getCenterAlignBorderStyle(final CellStyle style) {
		getBorderStyle(style);
	    style.setWrapText(true);
	    style.setAlignment(CellStyle.ALIGN_CENTER);
	    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    return style;
	}
	
	/**
	 * 產生至小數兩位格式
	 * @return
	 */
	public static CellStyle getBorderAndRateStyle(final ExcelWriter ew) {
		final CellStyle style = getBorderStyle(ew);
		style.setDataFormat(ew.createDataFormat().getFormat("0.00"));
		getCenterAlignBorderStyle(style);
		return style;
	}
	
	private static CellStyle getBorderStyle(final CellStyle style) {
		final short border = HSSFCellStyle.BORDER_THIN;
		style.setBorderBottom(border);
		style.setBorderTop(border);
		style.setBorderRight(border);
		style.setBorderLeft(border);
		return style;
	}
	
	private static CellStyle getBorderStyle(final ExcelWriter ew) {
		final CellStyle style = ew.createCellStyle();
		getBorderStyle(style);
		return style;
	}
	
	/** 欄位格式 + 邊框(靠右) */
	public static CellStyle getRightAlignBorderStyle(final ExcelWriter ew) {
		final CellStyle style = getBorderStyle(ew);
	    style.setWrapText(true);
	    style.setAlignment(CellStyle.ALIGN_RIGHT);
	    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	    return style;
	}
}
