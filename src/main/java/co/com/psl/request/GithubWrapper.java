package co.com.psl.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

@Component
public class GithubWrapper implements EnvironmentAware {

	private RelaxedPropertyResolver propertyResolver;

	@Override
	public void setEnvironment(Environment environment) {
		this.propertyResolver = new RelaxedPropertyResolver(environment);
	}

	public static final String GITHUB_API = "https://api.github.com/repos/";

	public JsonArray GetContributorsByRepo(String repo) throws IOException {
		String urlString = GITHUB_API + repo + "/stats/contributors?access_token="
				+ this.propertyResolver.getProperty("GITHUB_ACCESS_TOKEN");
		URL url;
		url = new URL(urlString);
		URLConnection conn = url.openConnection();
		InputStream inputStream = conn.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer);
		JsonParser parser = new JsonParser();
		return parser.parse(writer.toString()).getAsJsonArray();
	}
}
