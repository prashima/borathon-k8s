/*
 * *****************************************************
 * Copyright VMware, Inc. 2010-2012.  All Rights Reserved.
 * *****************************************************
 *
 * DISCLAIMER. THIS PROGRAM IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTIES OR CONDITIONS # OF ANY KIND, WHETHER ORAL OR WRITTEN,
 * EXPRESS OR IMPLIED. THE AUTHOR SPECIFICALLY # DISCLAIMS ANY IMPLIED
 * WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY # QUALITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
 */

package com.vmware.vsphere.client.connection.helpers;

import com.vmware.vsphere.client.connection.Connection;

public abstract class BaseHelper {
    final Connection connection;

    public BaseHelper(final Connection connection) {
        try {
            this.connection = connection.connect();
        } catch (Throwable t) {
            throw new HelperException(t);
        }
    }

    public class HelperException extends RuntimeException {
		private static final long serialVersionUID = 7620135477584513107L;

		public HelperException(Throwable cause) {
            super(cause);
        }
    }
}
