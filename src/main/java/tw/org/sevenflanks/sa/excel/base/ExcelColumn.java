package tw.org.sevenflanks.sa.excel.base;

import org.apache.poi.ss.usermodel.CellStyle;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 欄位定義
 * 
 * @author Rhys
 */
public class ExcelColumn<T, S> {
	
	// 顯示在Excel內的欄位名稱
	private String title;
	
	// 對應的Entity的Field名稱
	private String field;
	
	// 欄位寬度, null為維持預設, 0為Auto
	private Integer width;
	
	// 欄位Style
	private CellStyle style;
	
	// title Style
	private CellStyle titleStyle;
	
	public CellStyle getTitleStyle() {
		return titleStyle;
	}

	public ExcelColumn<T, S> setTitleStyle(CellStyle titleStyle) {
		this.titleStyle = titleStyle;
		return this;
	}

	// 該欄位Value的toString方法
	private Function<T, S> formater;
	
	// 該欄位的Method(由ExcelWriter自動處理)
	private Method method;
	
	@SuppressWarnings("unchecked")
	public ExcelColumn(String title, String field) {
		super();
		this.title = title;
		this.field = field;
		this.formater = t -> (S) t;
	}
	
	public ExcelColumn(String title, Function<T, S> formater) {
		super();
		this.title = title;
		this.formater = formater;
	}

	@Deprecated
	public ExcelColumn(String title, String field, Function<T, S> formater) {
		super();
		this.title = title;
		this.field = field;
		this.formater = formater;
	}
	
	public ExcelColumn<T, S> setStyle(CellStyle style) {
		this.style = style;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public S formate(Object obj) {
		return formater.apply((T)obj);
	}

    public ExcelColumn<T, S> setWidth(Integer width) {
		this.width = width;
		return this;
	}

	public Integer getWidth() {
		return width;
	}

	public String getTitle() {
		return title;
	}

	public String getField() {
		return field;
	}

	public Method getMethod() {
		return method;
	}

	public Function<T, S> getFormater() {
		return formater;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public CellStyle getStyle() {
		return style;
	}

}
