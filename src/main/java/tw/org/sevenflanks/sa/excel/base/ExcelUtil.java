package tw.org.sevenflanks.sa.excel.base;

public class ExcelUtil {
	
	/**
	 * 將poi計算用的rowNo轉為Excel公式可用的列號
	 * @param rowNo
	 * @return
	 */
	public static String parseRow(int rowNo) {
		return String.valueOf(rowNo + 1);
	}

	/**
	 * 將poi計算用的colNo轉為Excel公式可用的欄號
	 * @param colNo
	 * @return
	 */
	public static String parseCol(int colNo) {
		// 當作26進位處理, 因為0在Excel的欄位數字中不存在, 因此向右平移
    	colNo++;
        StringBuilder sb = new StringBuilder();
        while (colNo-- > 0) {
            sb.append((char)('A' + (colNo % 26)));
            colNo /= 26;
        }
        return sb.reverse().toString();
	}
	
	/**
	 * 將poi計算用的rowNo, colNo轉為Excel公式可用的列號欄號
	 * @param rowNo
	 * @param colNo
	 * @return
	 */
	public static String toColName(int rowNo, int colNo) {
        return parseCol(colNo) + parseRow(rowNo);
	}
	
}
