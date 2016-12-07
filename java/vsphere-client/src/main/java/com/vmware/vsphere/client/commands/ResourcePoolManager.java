package com.vmware.vsphere.client.commands;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ResourceAllocationInfo;
import com.vmware.vim25.ResourceConfigSpec;
import com.vmware.vim25.SharesInfo;
import com.vmware.vim25.SharesLevel;


public class ResourcePoolManager extends BaseCommand {
	public ResourcePoolManager() {
		
	}
	
	public String createResoucePool(String resPoolName, String clusterResPoolMoId, long cpuRes, long cpuLim, long memRes, long memLim)
			throws Exception{
		
		ManagedObjectReference clsMoref = new ManagedObjectReference();
		clsMoref.setType("ClusterComputeResource");
		clsMoref.setValue(clusterResPoolMoId);
		
		ResourceAllocationInfo cpuAllocInfo = new ResourceAllocationInfo();
		cpuAllocInfo.setReservation(cpuRes);
		cpuAllocInfo.setLimit(cpuLim);
		cpuAllocInfo.setExpandableReservation(true);
		SharesInfo cpuSharesInfo = new SharesInfo();
		cpuSharesInfo.setLevel(SharesLevel.NORMAL);
		cpuSharesInfo.setShares(0);
		cpuAllocInfo.setShares(cpuSharesInfo);
		cpuAllocInfo.setOverheadLimit(null);
		
		ResourceAllocationInfo memAllocInfo = new ResourceAllocationInfo();
		memAllocInfo.setReservation(memRes);
		memAllocInfo.setLimit(memLim);
		memAllocInfo.setExpandableReservation(true);
		SharesInfo memSharesInfo = new SharesInfo();
		memSharesInfo.setLevel(SharesLevel.NORMAL);
		memSharesInfo.setShares(0);
		memAllocInfo.setShares(memSharesInfo);
		memAllocInfo.setOverheadLimit(null);
		
		ResourceConfigSpec resConfigSpec = new ResourceConfigSpec();
		resConfigSpec.setChangeVersion("v1");
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		resConfigSpec.setLastModified(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
		resConfigSpec.setEntity(clsMoref);
		resConfigSpec.setCpuAllocation(cpuAllocInfo);
		resConfigSpec.setMemoryAllocation(memAllocInfo);
		
		ManagedObjectReference res = vcService.getVimPort().createResourcePool(clsMoref, resPoolName, resConfigSpec);
		return res.getValue();
	}

}
