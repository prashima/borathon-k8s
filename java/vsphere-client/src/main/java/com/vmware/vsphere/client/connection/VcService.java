/*
 * ******************************************************
 * Copyright VMware, Inc. 2010-2012.  All Rights Reserved.
 * ******************************************************
 *
 * DISCLAIMER. THIS PROGRAM IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTIES OR CONDITIONS # OF ANY KIND, WHETHER ORAL OR WRITTEN,
 * EXPRESS OR IMPLIED. THE AUTHOR SPECIFICALLY # DISCLAIMS ANY IMPLIED
 * WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY # QUALITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
 */

package com.vmware.vsphere.client.connection;

//import com.vmware.common.annotations.After;
//import com.vmware.common.annotations.Before;
//import com.vmware.common.annotations.Option;
import com.vmware.common.ssl.TrustAll;
import com.vmware.vsphere.client.connection.helpers.GetMOREF;
import com.vmware.vsphere.client.connection.helpers.WaitForValues;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for vCenter.
 */
public enum VcService {
	INSTANCE;

	private final Logger logger = LoggerFactory.getLogger(VcService.class);
	public static final String PROP_ME_NAME = "name";
    public static final String SVC_INST_NAME = "ServiceInstance";
    private Connection connection;
    private VimPortType vimPort;
    private ServiceContent serviceContent;
    private ManagedObjectReference rootRef;
    @SuppressWarnings("rawtypes")
    private Map headers;
    private WaitForValues waitForValues;
    private GetMOREF getMOREFs;

    public VimPortType getVimPort() {
		return vimPort;
	}

	public ServiceContent getServiceContent() {
		return serviceContent;
	}

	public ManagedObjectReference getRootRef() {
		return rootRef;
	}

	@SuppressWarnings("rawtypes")
	public Map getHeaders() {
		return headers;
	}

	public WaitForValues getWaitForValues() {
		return waitForValues;
	}

	public GetMOREF getGetMOREFs() {
		return getMOREFs;
	}

	public Connection getConnection() {
		return connection;
	}

	private VcService() {
		try {
			trustAll();
		} catch (Exception e) {
			String msg = "Failed to instantiate VcService while setting trust configuration.";
			logger.error(msg, e);
			throw new IllegalStateException(msg, e);
		}
		setConnection(ConnectionFactory.getConnection());
		connect();
	}

    /**
     * Sets up trusting SSL manager to allow us to work with servers that have self-signed certificates. This
     * weakens the Java SSL toolkit temporarily so that we can work with these servers with out having to manage
     * any certificates. The code that does this looks for the system property samples.trustAll=true if not
     * present the system uses normal SSL validation.
     *
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public void trustAll() throws NoSuchAlgorithmException, KeyManagementException {
        // NEVER do this in PRODUCTION!
        TrustAll.trust();
    }
	// By default assume we are talking to a vCenter
    
    Boolean hostConnection = Boolean.FALSE;

//    @Option(
//            name = "basic-com.vmware.vsphere.client.connection",
//            required = false,
//            description =
//                    "Turn off the use of SSO for connections. Useful for connecting to ESX or ESXi hosts.",
//            parameter = false
//    )
    public void setHostConnection(final Boolean value) {
        // NOTE: the common framework will insert a "Boolean.TRUE" object on
        // options that have parameter = false set. This indicates they
        // are boolean flag options not string parameter options.
        this.hostConnection = value;
    }

    /**
     * Use this method to get a reference to the service instance itself.
     * <p>
     *
     * @return a managed object reference referring to the service instance itself.
     */
    public ManagedObjectReference getServiceInstanceReference() {
        return connection.getServiceInstanceReference();
    }

    /**
     * A method for dependency injection of the com.vmware.vsphere.client.connection object.
     * <p>
     *
     * @param connect the com.vmware.vsphere.client.connection object to use for this POJO
     * @see com.vmware.connection.Connection
     */
//    @Option(name = "com.vmware.vsphere.client.connection", type = Connection.class)
    public void setConnection(Connection connect) {
        this.connection = connect;
    }

    /**
     * connects this object, returns itself to allow for method chaining
     *
     * @return a connected reference to itself.
     */
//    @Before
    public Connection connect() {

        if(hostConnection) {
            // construct a BasicConnection object to use for
            connection = basicConnectionFromConnection(connection);
        }

        try {
            connection.connect();
            this.waitForValues = new WaitForValues(connection);
            this.getMOREFs = new GetMOREF(connection);
            this.headers = connection.getHeaders();
            this.vimPort = connection.getVimPort();
            this.serviceContent = connection.getServiceContent();
            this.rootRef = serviceContent.getRootFolder();
        }
        catch (ConnectionException e) {
            // SSO or Basic com.vmware.vsphere.client.connection exception has occurred
            e.printStackTrace();
            // not the best form, but without a com.vmware.vsphere.client.connection these samples are pointless.
            System.err.println("No valid com.vmware.vsphere.client.connection available. Exiting now.");
            System.exit(0);
        }
        return connection;
    }

    public BasicConnection basicConnectionFromConnection(final Connection original) {
        BasicConnection connection = new BasicConnection();
        connection.setUrl(original.getUrl());
        connection.setUsername(original.getUsername());
        connection.setPassword(original.getPassword());
        return connection;
    }

    /**
     * disconnects this object and returns a reference to itself for method chaining
     *
     * @return a disconnected reference to itself
     */
//    @After
    public Connection disconnect() {
        this.waitForValues = null;
        try {
            connection.disconnect();
        } catch (Throwable t) {
            throw new ConnectionException(t);
        }
        return connection;
    }

    public class ConnectionException extends RuntimeException {

		private static final long serialVersionUID = 7442764247690486098L;

		public ConnectionException(Throwable cause) {
            super(cause);
        }

        public ConnectionException(String message) {
            super(message);
        }
    }
}
