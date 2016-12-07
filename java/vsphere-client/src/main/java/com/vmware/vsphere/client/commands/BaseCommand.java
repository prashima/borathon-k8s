package com.vmware.vsphere.client.commands;

import com.vmware.vsphere.client.connection.VcService;

public abstract class BaseCommand {

	protected VcService vcService = VcService.INSTANCE;
}
