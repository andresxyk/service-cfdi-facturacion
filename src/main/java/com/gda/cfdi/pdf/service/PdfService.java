package com.gda.cfdi.pdf.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gda.cfdi.pdf.dto.PdfInfoDto;
import com.gda.cfdi.pdf.dto.TFacturaDto;
import com.gda.cfdi.pdf.dto.TOrdenSucursalFacDto;
import com.gda.cfdi.pdf.service.template.ITemplatePdfServiceImpl;
import com.itextpdf.text.DocumentException;

import mx.gob.sat.cfd._3.Comprobante;
import mx.gob.sat.cfd.pagos.Pagos;
import mx.gob.sat.timbrefiscaldigital.TimbreFiscalDigital;

@Service
public class PdfService {
	
	private static final Logger log = LoggerFactory.getLogger(PdfService.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private ConsultaService consultaService;
	
	@Autowired
	private ITemplatePdfServiceImpl iTemplatePdfService;
	
	public String generarPdf(Integer kfactura, Integer cmarca, Integer csucursal) throws DocumentException, IOException, Exception{
		
		ArrayList<Integer> arrPrado = new ArrayList<Integer>(Arrays.asList(117,118,126,127,128,129,130,132,134,138,139,140,141,142,143,196,198,199,200));
		ArrayList<Integer> arrLean = new ArrayList<Integer>(Arrays.asList(115,116,119,120,121,122,123,124,125,131,133,135,136,137,177,146,197));
		
		TFacturaDto facturaDto = consultaService.getTFacturaById(kfactura);
		String ruta = "";
		String xml = facturaDto.getXmlTimbrado();
		System.out.println(xml);
		if(facturaDto!=null) {
			if(!xml.isEmpty() && xml.trim().length()>5) {
				Comprobante comprobante = this.createComplementoFromXml(xml);
				PdfInfoDto infoPDF = new PdfInfoDto();
				infoPDF.setKfactura(kfactura);
				infoPDF.setComplementoConcepto(false);
				if(xml.contains("PorCuentadeTerceros")){
					infoPDF.setComplementoConcepto(true);
				}
				log.info("cfdi:ComplementoConcepto:"+infoPDF.getComplementoConcepto());
				String dirSucursal = consultaService.getDireccionSucursalFactura(kfactura);
				
				if(csucursal.equals(219)){
					dirSucursal = "CALLE OJINAGA 423, ZONA CENTRO C.P. 31000 CHIHUAHUA, CHIHUAHUA";
				}else if(csucursal.equals(218)){
					dirSucursal = "CALLE GENERAL RETANA 905, SAN FELIPE I C.P. 31203 CHIHUAHUA, CHIHUAHUA";
				}else if(csucursal.equals(217)){
					dirSucursal = "AV. CANTERA 9139 INT. 110, MISIONES C.P. 31115 CHIHUAHUA, CHIHUAHUA";
				}else if(csucursal.equals(220)){
					dirSucursal = "CALLE LERDO DE TEJADA 504, CAMARGO C.P. 33700 CAMARGO, CHIHUAHUA";
				}else if(csucursal.equals(216)){
					dirSucursal = "AV. FREDOR DOSTOYESVSKI 1308, ALAMEDAD C.P. 31136 CHIHUAHUA, CHIHUAHUA";
				}
				
				infoPDF.setDirSucursal(dirSucursal);
				
				Integer centidadlegal = null;
				if (cmarca == 1) {
					centidadlegal = 1;
				}else if (cmarca == 4) {
					centidadlegal = 5;
				}else if (cmarca == 7) {
					if(arrPrado.contains(csucursal)){
						System.out.println("**** Prado *****");
						centidadlegal = 7;				
					}else if(arrLean.contains(csucursal)){
						System.out.println("**** Lean *****");
						centidadlegal = 8;	
					}				
				}else if (cmarca == 5) {
					centidadlegal = 6;
				}else if (cmarca == 15) {
					centidadlegal = 6;
				}
				
				String dirEmisorSucursal = consultaService.getDireccionFiscalEmisor(centidadlegal);
				infoPDF.setDirFiscalEmisor(dirEmisorSucursal);				
				List<TOrdenSucursalFacDto> listTosf = consultaService.getTOrdenSucursalFacByKfactura(kfactura);
				infoPDF.setConsecutivo(listTosf.get(0).getUorden());
				String nombrepaciente = consultaService.getPacienteFactura(kfactura);
				infoPDF.setNombrePaciente(nombrepaciente);
				String strMetodoPago = consultaService.getConceptoMetodoPago(comprobante.getMetodoPago().value());
				infoPDF.setDescripcionMetodoPago(strMetodoPago);
				String descripcionusocfdi = consultaService.getUsoCfdi(comprobante.getReceptor().getUsoCFDI().value());
				infoPDF.setDescripcionUsoCfdi(descripcionusocfdi);
				
				infoPDF.setCadenaOriginal(facturaDto.getCadenaOriginal());
				
				
				switch (cmarca) {
					case 1:
						log.info("OLAB*****");
						ruta = iTemplatePdfService.CrearPdfMarcaOlab(comprobante, infoPDF);
						break;
					case 4:
						log.info("*******************AZTECA*****");
						ruta = iTemplatePdfService.CrearPdfMarcaAzteca(comprobante, infoPDF);
						break;
					case 5:
						log.info("SWISSLAB*****");
						ruta = iTemplatePdfService.CrearPdfMarcaSwiss(comprobante, infoPDF);
						break;
					case 15:
						log.info("LIACSA*****");
						ruta = iTemplatePdfService.CrearPdfMarcaLiacsa(comprobante, infoPDF);
						break;
					case 7:
						log.info("JENNER*****");
						ruta = iTemplatePdfService.CrearPdfMarcaJenner(comprobante, infoPDF);
						break;
					default:
						break;
				} 
			}
		}
		return ruta;		
	}
	
	
	private Comprobante createComplementoFromXml(String xml) throws JAXBException {
		Comprobante comprobante = null;
		JAXBContext jaxbContext;
		List<Class<?>> classesMarshall = new ArrayList<Class<?>>();

		if (xml.toLowerCase().contains("<tfd:TimbreFiscalDigital".toLowerCase())) {
			classesMarshall.add(TimbreFiscalDigital.class);
		}
		classesMarshall.add(Comprobante.class);
		classesMarshall.add(Pagos.class);
		jaxbContext = JAXBContext.newInstance(classesMarshall.toArray(new Class<?>[classesMarshall.size()]));
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		StringReader reader = new StringReader(this.fixXml(xml));
		comprobante = (Comprobante) unmarshaller.unmarshal(reader);

		return comprobante;
	}
	
	private static String fixXml(String xml) {
		xml = xml.replace("/cfd/3\"xmlns", "/cfd/3\" xmlns");
		xml = xml.replace("instance\"xsi", "instance\" xsi");
		xml = xml.replace("cfd/3 http", "cfd/3 http");
		return xml;
	}
	

}
