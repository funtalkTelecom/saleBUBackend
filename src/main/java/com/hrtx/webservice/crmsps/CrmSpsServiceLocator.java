/**
 * CrmSpsServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.hrtx.webservice.crmsps;

public class CrmSpsServiceLocator extends org.apache.axis.client.Service implements CrmSpsService {

    public CrmSpsServiceLocator() {
    }


    public CrmSpsServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CrmSpsServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for crmSpsServiceHttpPort
    private String crmSpsServiceHttpPort_address = "http://124.202.134.11:8080/crm1/services/crmSpsServiceHttpPort";

    public String getcrmSpsServiceHttpPortAddress() {
        return crmSpsServiceHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String crmSpsServiceHttpPortWSDDServiceName = "crmSpsServiceHttpPort";

    public String getcrmSpsServiceHttpPortWSDDServiceName() {
        return crmSpsServiceHttpPortWSDDServiceName;
    }

    public void setcrmSpsServiceHttpPortWSDDServiceName(String name) {
        crmSpsServiceHttpPortWSDDServiceName = name;
    }

    public CrmSpsServicePortType getcrmSpsServiceHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(crmSpsServiceHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getcrmSpsServiceHttpPort(endpoint);
    }

    public com.hrtx.webservice.crmsps.CrmSpsServicePortType getcrmSpsServiceHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.hrtx.webservice.crmsps.CrmSpsServiceHttpPortSoapBindingStub _stub = new com.hrtx.webservice.crmsps.CrmSpsServiceHttpPortSoapBindingStub(portAddress, this);
            _stub.setPortName(getcrmSpsServiceHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setcrmSpsServiceHttpPortEndpointAddress(String address) {
        crmSpsServiceHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.hrtx.webservice.crmsps.CrmSpsServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.hrtx.webservice.crmsps.CrmSpsServiceHttpPortSoapBindingStub _stub = new com.hrtx.webservice.crmsps.CrmSpsServiceHttpPortSoapBindingStub(new java.net.URL(crmSpsServiceHttpPort_address), this);
                _stub.setPortName(getcrmSpsServiceHttpPortWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("crmSpsServiceHttpPort".equals(inputPortName)) {
            return getcrmSpsServiceHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://crmsps.webservice.namespace", "crmSpsService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://crmsps.webservice.namespace", "crmSpsServiceHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("crmSpsServiceHttpPort".equals(portName)) {
            setcrmSpsServiceHttpPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
