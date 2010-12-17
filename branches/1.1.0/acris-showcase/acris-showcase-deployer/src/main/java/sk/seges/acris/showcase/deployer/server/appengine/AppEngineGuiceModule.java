/**
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package sk.seges.acris.showcase.deployer.server.appengine;

import org.openid4java.consumer.ConsumerManager;
import org.openid4java.util.HttpFetcher;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

public class AppEngineGuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(HttpFetcher.class).to(AppEngineHttpFetcher.class).in(Scopes.SINGLETON);
		bind(ConsumerManager.class).to(AppengineConsumerManager.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	public URLFetchService providerUrlFetchService() {
		return URLFetchServiceFactory.getURLFetchService();
	}
}
