package com.vmware.photon.controller.common.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VcsProperties extends Properties {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(VcsProperties.class);
	private static VcsProperties instance = null;
	
	private static String CLUSTER_MANAGER_SCRIPTS_DIR_KEY = "clusterManagerScriptsDir";
	private static String VCS_STORAGE_PATH_KEY = "vcsStoragePath";
	
	public static synchronized void init(String file) throws Exception {
		logger.info("Initializing VcsProperties with {}", file);
		if (instance == null) {
			instance = new VcsProperties();
		}
		FileInputStream in = new FileInputStream(file);
		instance.load(in);
		in.close();
	}
	
	public static String getClusterManagerScriptsDir() {
		if (instance == null) {
			throw new RuntimeException("VcsProperties not initialized!!!");
		}
		return instance.getProperty(CLUSTER_MANAGER_SCRIPTS_DIR_KEY);
	}
	
	public static String getVcsStoragePath() {
		if (instance == null) {
			throw new RuntimeException("VcsProperties not initialized!!!");
		}
		return instance.getProperty(VCS_STORAGE_PATH_KEY);		
	}

}
