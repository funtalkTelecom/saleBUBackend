/**
 * CrmSpsServiceHttpPortSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.hrtx.webservice.crmsps;

public class CrmSpsServiceHttpPortSoapBindingSkeleton implements CrmSpsServicePortType, org.apache.axis.wsdl.Skeleton {
    private CrmSpsServicePortType impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://crmsps.webservice.namespace", "in0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("exchange", _params, new javax.xml.namespace.QName("http://crmsps.webservice.namespace", "out"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://crmsps.webservice.namespace", "exchange"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("exchange") == null) {
            _myOperations.put("exchange", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("exchange")).add(_oper);
    }

    public CrmSpsServiceHttpPortSoapBindingSkeleton() {
        this.impl = new CrmSpsServiceHttpPortSoapBindingImpl();
    }

    public CrmSpsServiceHttpPortSoapBindingSkeleton(CrmSpsServicePortType impl) {
        this.impl = impl;
    }
    public String exchange(String in0) throws java.rmi.RemoteException
    {
        String ret = impl.exchange(in0);
        return ret;
    }

}
