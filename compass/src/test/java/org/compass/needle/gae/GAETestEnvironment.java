/*
 * Copyright 2004-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.compass.needle.gae;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.apphosting.api.ApiProxy;

/**
 * @author kimchy
 */
public class GAETestEnvironment implements ApiProxy.Environment {

	public String getAppId() {
		return "test";
	}

	public String getVersionId() {
		return "1.0";
	}

	public String getEmail() {
		throw new UnsupportedOperationException();
	}

	public boolean isLoggedIn() {
		throw new UnsupportedOperationException();
	}

	public boolean isAdmin() {
		throw new UnsupportedOperationException();
	}

	public String getAuthDomain() {
		throw new UnsupportedOperationException();
	}

	public String getRequestNamespace() {
		return "";
	}

	public Map<String, Object> getAttributes() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("com.google.appengine.server_url_key", "http://localhost:8080");
		return map;
	}

	public String getModuleId() {
		// TODO Auto-generated method stub
		return UUID.randomUUID().toString();
	}

	public long getRemainingMillis() {
		// TODO Auto-generated method stub
		return 1000;
	}
}