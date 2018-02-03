package nohi.doc.meta;

public class DocMeta {
	private String id; // 文档ID
	private String name;// 文档名称
	private String template;// 文档模板
	private String conf;// 文档具体属性配置文件


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getConf() {
		return conf;
	}

	public void setConf(String conf) {
		this.conf = conf;
	}
}
