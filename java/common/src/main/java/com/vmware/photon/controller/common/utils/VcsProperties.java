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
	private static String CHECK_KUBE_PATH_KEY = "cubeStatusPath";
	private static String TENANT_SUPPORT_ENABLED = "tenantSupportEnabled";
	private static String VCH_CLIENT_ENABLED = "vchClientEnabled";
	private static String DOCKER_ENGINE_IP = "dockerEngineIp";
	private static String DOCKER_ENGINE_PORT = "dockerEnginePort";
	private static String DOCKER_BINARY = "dockerBinary";
	private static String K8S_IMAGE = "k8sImage";

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

	public static String getKubeStatusPath() {
		if (instance == null) {
			throw new RuntimeException("csProperties not initialized!!!");
		}
		return instance.getProperty(CHECK_KUBE_PATH_KEY);
	}

	public static boolean isTenantSupportEnabled() {
		if (instance == null) {
			throw new RuntimeException("tenantSupportEnabled not initialized!!");
		}
		return Boolean.getBoolean(instance.getProperty(TENANT_SUPPORT_ENABLED));
	}

	public static String getDockerEngineIp() {
		if (instance == null) {
			throw new RuntimeException("dockerEngineIp not initialized!!");
		}
		return instance.getProperty(DOCKER_ENGINE_IP);
	}

	public static String getDockerEnginePort() {
		if (instance == null) {
			throw new RuntimeException("dockerEnginePort not initialized!!");
		}
		String port = instance.getProperty(DOCKER_ENGINE_PORT);
		if (getDockerEngineIp() != null && (port == null || port.isEmpty())) {
			port = "2376";
		}
		return port;
	}

	public static boolean isVchClientEnabled() {
		if (instance == null) {
			throw new RuntimeException("dockerEnginePort not initialized!!");
		}
		String vchClientEnabled = instance.getProperty(VCH_CLIENT_ENABLED, "true");
		return Boolean.parseBoolean(vchClientEnabled);
	}

	public static String getDockerBinary() {
		if (instance == null) {
			throw new RuntimeException("dockerBinary not initialized!!");
		}
		return instance.getProperty(DOCKER_BINARY, "/usr/local/bin/docker");
	}
	
	public static String getK8sImage() {
		if (instance == null) {
			throw new RuntimeException("k8sImage not initialized!!");
		}
		return instance.getProperty(K8S_IMAGE, "luomiao/vcs-k8s-node:v0.3");
	}
}
