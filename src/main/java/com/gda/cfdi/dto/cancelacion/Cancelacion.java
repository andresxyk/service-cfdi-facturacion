//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantaci�n de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perder�n si se vuelve a compilar el esquema de origen. 
// Generado el: 2022.01.19 a las 10:09:18 AM CST 
//


package com.gda.cfdi.dto.cancelacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Folios">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Folio">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="UUID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="Motivo" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" />
 *                           &lt;attribute name="FolioSustitucion" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="Fecha" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="RfcEmisor" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "folios"
})
@XmlRootElement(name = "Cancelacion")
public class Cancelacion {

    @XmlElement(name = "Folios", required = true)
    protected Cancelacion.Folios folios;
    @XmlAttribute(name = "Fecha", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fecha;
    @XmlAttribute(name = "RfcEmisor", required = true)
    protected String rfcEmisor;

    /**
     * Obtiene el valor de la propiedad folios.
     * 
     * @return
     *     possible object is
     *     {@link Cancelacion.Folios }
     *     
     */
    public Cancelacion.Folios getFolios() {
        return folios;
    }

    /**
     * Define el valor de la propiedad folios.
     * 
     * @param value
     *     allowed object is
     *     {@link Cancelacion.Folios }
     *     
     */
    public void setFolios(Cancelacion.Folios value) {
        this.folios = value;
    }

    /**
     * Obtiene el valor de la propiedad fecha.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFecha() {
        return fecha;
    }

    /**
     * Define el valor de la propiedad fecha.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFecha(XMLGregorianCalendar value) {
        this.fecha = value;
    }

    /**
     * Obtiene el valor de la propiedad rfcEmisor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRfcEmisor() {
        return rfcEmisor;
    }

    /**
     * Define el valor de la propiedad rfcEmisor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRfcEmisor(String value) {
        this.rfcEmisor = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Folio">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="UUID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="Motivo" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" />
     *                 &lt;attribute name="FolioSustitucion" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "folio"
    })
    public static class Folios {

        @XmlElement(name = "Folio", required = true)
        protected Cancelacion.Folios.Folio folio;

        /**
         * Obtiene el valor de la propiedad folio.
         * 
         * @return
         *     possible object is
         *     {@link Cancelacion.Folios.Folio }
         *     
         */
        public Cancelacion.Folios.Folio getFolio() {
            return folio;
        }

        /**
         * Define el valor de la propiedad folio.
         * 
         * @param value
         *     allowed object is
         *     {@link Cancelacion.Folios.Folio }
         *     
         */
        public void setFolio(Cancelacion.Folios.Folio value) {
            this.folio = value;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="UUID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="Motivo" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" />
         *       &lt;attribute name="FolioSustitucion" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Folio {

            @XmlAttribute(name = "UUID", required = true)
            protected String uuid;
            @XmlAttribute(name = "Motivo", required = true)
            protected String motivo;
            @XmlAttribute(name = "FolioSustitucion", required = true)
            protected String folioSustitucion;

            /**
             * Obtiene el valor de la propiedad uuid.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getUUID() {
                return uuid;
            }

            /**
             * Define el valor de la propiedad uuid.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setUUID(String value) {
                this.uuid = value;
            }

            /**
             * Obtiene el valor de la propiedad motivo.
             * 
             */
            public String getMotivo() {
                return motivo;
            }

            /**
             * Define el valor de la propiedad motivo.
             * 
             */
            public void setMotivo(String value) {
                this.motivo = value;
            }

            /**
             * Obtiene el valor de la propiedad folioSustitucion.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFolioSustitucion() {
                return folioSustitucion;
            }

            /**
             * Define el valor de la propiedad folioSustitucion.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFolioSustitucion(String value) {
                this.folioSustitucion = value;
            }

        }

    }

}
