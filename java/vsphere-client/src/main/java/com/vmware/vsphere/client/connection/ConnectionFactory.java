package com.vmware.vsphere.client.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vsphere.client.config.VcClientProperties;

public final class ConnectionFactory {
	private static final Logger logger = LoggerFactory
			.getLogger(ConnectionFactory.class);
	private static Connection connection = null;

	private ConnectionFactory() {
	}

	public static Connection getConnection() {
		if (connection == null || !connection.isConnected()) {
			try {
				//closeConnection();

				connection = new SsoConnection();
				connection.setUrl(VcClientProperties.INSTANCE.getUrl());
				connection.setPassword(VcClientProperties.INSTANCE
						.getPassword());
				connection.setUsername(VcClientProperties.INSTANCE
						.getUsername());
				//connection.connect();
			} catch (Exception e) {
				logger.error("Error occurred while connecting to vCenter %s",
						connection.getUrl(), e);
				throw new ConnectionException(e.getMessage(), e);
			}
		}
		return connection;
	}

	private static void closeConnection() {
		if (connection == null) {
			return;
		}

		try {
			connection.disconnect();
		} catch (Exception e) {
			logger.error("Erroc occurred while attempting to close vCenter connection.",
					connection.getUrl(), e);
		}
	}
}
