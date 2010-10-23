package sk.seges.acris.site.server.service;

import sk.seges.acris.site.shared.domain.api.WebSettingsData;
import sk.seges.acris.site.shared.domain.dto.WebSettingsDTO;
import sk.seges.acris.site.shared.service.IWebSettingsService;

public class MockWebSettingsService implements IWebSettingsService {

	private static final long serialVersionUID = 1337432311249783469L;

	protected Boolean localeSensitiveServer;

	protected String googleAnalyticsScript;

	public MockWebSettingsService(String googleAnalyticsScript, Boolean localeSensitiveServer) {
		this.googleAnalyticsScript = googleAnalyticsScript;
		this.localeSensitiveServer = localeSensitiveServer;
	}

	public Boolean getLocaleSensitiveServer() {
		return localeSensitiveServer;
	}

	public void setLocaleSensitiveServer(Boolean localeSensitiveServer) {
		this.localeSensitiveServer = localeSensitiveServer;
	}

	public String getGoogleAnalyticsScript() {
		return googleAnalyticsScript;
	}

	public void setGoogleAnalyticsScript(String googleAnalyticsScript) {
		this.googleAnalyticsScript = googleAnalyticsScript;
	}

	@Override
	public WebSettingsData getWebSettings(String webId) {
		WebSettingsData webSettings = new WebSettingsDTO();
		webSettings.setWebId(webId);
		webSettings.setLanguage("en");

		webSettings.setTopLevelDomain("http://" + webId + "/");

		webSettings.setAnalyticsScriptData(googleAnalyticsScript);

		return webSettings;
	}

	@Override
	public void saveWebSettings(WebSettingsData webSettingsData) {
	}
}