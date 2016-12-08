package com.vmware.vsphere.client.commands;

import java.util.List;
import java.util.Map;

import com.vmware.vim25.GuestInfo;
import com.vmware.vim25.GuestNicInfo;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFaultFaultMsg;

public class VmNetwork extends BaseCommand {

	public static final String CONNECTED_TRUE = "True";
	public static final String CONNECTED_FALSE = "False";
	public static final String CONNECTED_UNKNOWN = "Unknown";

	public Network getNetworkWithRetry(String vm, int retryCount, long intervalMillis) {
		Network network = new Network();
		network.setIsConnected("Unknown");
		while (!network.getIsConnected().equals(CONNECTED_TRUE) && retryCount > 0) {
			network = getNetwork(vm);
			try {
				Thread.sleep(intervalMillis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return network;
	}

	public Network getNetwork(String vm) {
		ManagedObjectReference moRef = new ManagedObjectReference();
		moRef.setType("VirtualMachine");
		moRef.setValue(vm);
		Map<String, Object> result = null;
		Network network = new Network();

		try {
			result = vcService.getGetMOREFs().entityProps(moRef,
					new String[] { "guest" });
			if (result == null || result.size() != 1) {
				throw new IllegalStateException(
						"No guest property found for VM " + vm);
			}
			for (Object value : result.values()) {
				GuestInfo guestInfo = (GuestInfo) value;
				String ipAddress = guestInfo.getIpAddress();

				if (ipAddress == null || ipAddress.isEmpty()) {
					network.setIsConnected("Unknown");
					break;
				}

				network.setIpAddress(ipAddress);

				List<GuestNicInfo> nicInfo = guestInfo.getNet();
				if (nicInfo == null || nicInfo.isEmpty()) {
					network.setIsConnected("Unknown");
					break;
				}

				network.setIsConnected("True");
				network.setNetmask("255.255.252.0");

				for (GuestNicInfo info : nicInfo) {
					String networkName = info.getNetwork();
					if (networkName != null && !networkName.isEmpty()) {
						network.setNetwork(networkName);
						for (String ip : info.getIpAddress()) {
							if (ip.equals(network.getIpAddress())) {
								network.setMacAddress(info.getMacAddress());
							}
						}
					}
				}
			}
		} catch (InvalidPropertyFaultMsg | RuntimeFaultFaultMsg e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return network;
	}

	public static class Network {
		private String ipAddress;
		private String macAddress;
		private String isConnected;
		private String network;
		private String netmask;

		public String getIpAddress() {
			return ipAddress;
		}

		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}

		public String getMacAddress() {
			return macAddress;
		}

		public void setMacAddress(String macAddress) {
			this.macAddress = macAddress;
		}

		public String getIsConnected() {
			return isConnected;
		}

		public void setIsConnected(String isConnected) {
			this.isConnected = isConnected;
		}

		public String getNetwork() {
			return network;
		}

		public void setNetwork(String network) {
			this.network = network;
		}

		public String getNetmask() {
			return netmask;
		}

		public void setNetmask(String netmask) {
			this.netmask = netmask;
		}
	}
}
