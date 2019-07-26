
public class DataElement {
	private Integer code;
	private Integer size;
	private Boolean variable;
	private Integer maxNumberOfDigits;
	private String type;
	
	public DataElement(Integer code, Integer size, Boolean variable, Integer maxNumberOfDigits, String type) {
		this.code = code;
		this.size = size;
		this.variable = variable;
		this.maxNumberOfDigits = maxNumberOfDigits;
		this.type = type;
	}

	public Integer getCode() {
		return code;
	}

	public Integer getSize() {
		return size;
	}

	public Boolean getVariable() {
		return variable;
	}

	public Integer getMaxNumberOfDigits() {
		return maxNumberOfDigits;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "DataElement [code=" + code + ", size=" + size + ", variable=" + variable + ", maxNumberOfDigits="
				+ maxNumberOfDigits + ", type=" + type + "]";
	}
	
	
}
