package co.com.psl.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
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
		StringBuilder getContributorsUrl = new StringBuilder();
		getContributorsUrl.append(GITHUB_API);
		getContributorsUrl.append("/stats/contributors?access_token=");
		getContributorsUrl.append(this.propertyResolver.getProperty("GITHUB_ACCESS_TOKEN"));
		JsonParser parser = new JsonParser();
		String httpResponse = makeHttpRequest(getContributorsUrl.toString(), HttpMethod.GET);
		return parser.parse(httpResponse).getAsJsonArray();
	}
	
	public String makeHttpRequest(String url, HttpMethod method) throws IOException{
		URL urlToRequest = new URL(url);
		HttpURLConnection httpConnection = (HttpURLConnection) urlToRequest.openConnection();
		httpConnection.setRequestMethod(method.toString());
		InputStream inputStream = httpConnection.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer);
		return writer.toString();
	}
}
