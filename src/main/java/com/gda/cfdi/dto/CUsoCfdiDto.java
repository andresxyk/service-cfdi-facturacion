package com.gda.cfdi.dto;

public class CUsoCfdiDto {
    private Integer cusocfdi;
    private String sclaveusocfdi;
    private String susocfdi;
    private Boolean baplicapersonafisica;
    private Boolean baplicapersonamoral;
    private Integer cestadoregistro;
    
	public Integer getCusocfdi() {
		return cusocfdi;
	}
	public void setCusocfdi(Integer cusocfdi) {
		this.cusocfdi = cusocfdi;
	}
	public String getSclaveusocfdi() {
		return sclaveusocfdi;
	}
	public void setSclaveusocfdi(String sclaveusocfdi) {
		this.sclaveusocfdi = sclaveusocfdi;
	}
	public String getSusocfdi() {
		return susocfdi;
	}
	public void setSusocfdi(String susocfdi) {
		this.susocfdi = susocfdi;
	}
	public Boolean getBaplicapersonafisica() {
		return baplicapersonafisica;
	}
	public void setBaplicapersonafisica(Boolean baplicapersonafisica) {
		this.baplicapersonafisica = baplicapersonafisica;
	}
	public Boolean getBaplicapersonamoral() {
		return baplicapersonamoral;
	}
	public void setBaplicapersonamoral(Boolean baplicapersonamoral) {
		this.baplicapersonamoral = baplicapersonamoral;
	}
	public Integer getCestadoregistro() {
		return cestadoregistro;
	}
	public void setCestadoregistro(Integer cestadoregistro) {
		this.cestadoregistro = cestadoregistro;
	}
	
	@Override
	public String toString() {
		return "CUsoCfdiEntity [cusocfdi=" + cusocfdi + ", sclaveusocfdi=" + sclaveusocfdi + ", susocfdi=" + susocfdi
				+ ", baplicapersonafisica=" + baplicapersonafisica + ", baplicapersonamoral=" + baplicapersonamoral
				+ ", cestadoregistro=" + cestadoregistro + "]";
	}
}
