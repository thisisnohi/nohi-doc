
package nohi.doc.excel.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestListVO
{
	private int id;
	private String name;
	private Integer age;
	private Date date;
	private Double amt;
	
	private BigDecimal bd;
	private String test;
	
	private Map abcMap;
	
	public Map getAbcMap() {
		if (null == abcMap) {
			abcMap = new HashMap();
		}
		return abcMap;
	}

	public void setAbcMap(Map abcMap) {
		this.abcMap = abcMap;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Integer getAge()
	{
		return age;
	}

	public void setAge(Integer age)
	{
		this.age = age;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public Double getAmt()
	{
		return amt;
	}

	public void setAmt(Double amt)
	{
		this.amt = amt;
	}

	public BigDecimal getBd()
	{
		return bd;
	}

	public void setBd(BigDecimal bd)
	{
		this.bd = bd;
	}
}
