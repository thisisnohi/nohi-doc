package nohi.doc.pdf.vo;

import java.math.BigDecimal;
import java.util.Date;

public class PdfInnerVO {

	private int int_1;
	private Integer integer_1;

	private String string_1;
	private double double_1;
	private Double double_2;

	private BigDecimal bd;

	private Date date;

	private String codeCd;
	
	
	public String getCodeCd() {
		return codeCd;
	}

	public void setCodeCd(String codeCd) {
		this.codeCd = codeCd;
	}

	public int getInt_1() {
		return int_1;
	}

	public void setInt_1(int int_1) {
		this.int_1 = int_1;
	}

	public Integer getInteger_1() {
		return integer_1;
	}

	public void setInteger_1(Integer integer_1) {
		this.integer_1 = integer_1;
	}

	public String getString_1() {
		return string_1;
	}

	public void setString_1(String string_1) {
		this.string_1 = string_1;
	}

	public double getDouble_1() {
		return double_1;
	}

	public void setDouble_1(double double_1) {
		this.double_1 = double_1;
	}

	public Double getDouble_2() {
		return double_2;
	}

	public void setDouble_2(Double double_2) {
		this.double_2 = double_2;
	}

	public BigDecimal getBd() {
		return bd;
	}

	public void setBd(BigDecimal bd) {
		this.bd = bd;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
