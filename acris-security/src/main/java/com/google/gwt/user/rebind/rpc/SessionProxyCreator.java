/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.user.rebind.rpc;

import sk.seges.acris.security.client.proxy.SecureRemoteServiceProxy;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.client.rpc.impl.RemoteServiceProxy;

/**
 * Creates a client-side proxy for a
 * {@link com.google.gwt.user.client.rpc.RemoteService RemoteService} interface
 * as well as the necessary type and field serializers.
 */
public class SessionProxyCreator extends ProxyCreator {

	public SessionProxyCreator(JClassType serviceIntf) {
		super(serviceIntf);
	}

	protected Class<? extends RemoteServiceProxy> getProxySupertype() {
		return SecureRemoteServiceProxy.class;
	}
}
