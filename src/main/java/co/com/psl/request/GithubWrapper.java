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
	private JsonParser parser = new JsonParser();

	@Override
	public void setEnvironment(Environment environment) {
		this.propertyResolver = new RelaxedPropertyResolver(environment);
	}

	public static final String GITHUB_API = "https://api.github.com/repos/";
	public static final String GITHUB_API_CONTRIBUTORS = "/stats/contributors?access_token=" ;
	public static final String GITHUB_API_TREES_RECURSIVE = "/git/trees/master?recursive=1&access_token=";
	public static final String GITHUB_API_ADDITIONS_PER_WEEK = "/stats/code_frequency";

	public JsonArray getContributorsByRepo(String repository) throws IOException {
		StringBuilder getContributorsUrl = new StringBuilder()
			.append(GITHUB_API)
			.append(repository)
			.append(GITHUB_API_CONTRIBUTORS)
			.append(this.propertyResolver.getProperty("GITHUB_ACCESS_TOKEN"));
		String httpResponse = makeHttpRequest(getContributorsUrl.toString(), HttpMethod.GET);
		return parser.parse(httpResponse).getAsJsonArray();
	}
	
	public JsonArray getNumberOfFilesByRepository(String repository) throws IOException{
		StringBuilder getTreeFilesUrl = new StringBuilder()
			.append(GITHUB_API)
			.append(repository)
			.append(GITHUB_API_TREES_RECURSIVE)
			.append(this.propertyResolver.getProperty("GITHUB_ACCESS_TOKEN"));
		String httpResponse = makeHttpRequest(getTreeFilesUrl.toString(), HttpMethod.GET);
		return parser.parse(httpResponse).getAsJsonObject().get("tree").getAsJsonArray();
	}
	
	public JsonArray getNumberOfLinesModificationsFromDate(String repository, String date, String branch) throws IOException{
		StringBuilder commitsUrl = new StringBuilder()
			.append(GITHUB_API)
			.append(repository)
			.append(GITHUB_API_ADDITIONS_PER_WEEK);
		String httpResponse = makeHttpRequest(commitsUrl.toString(), HttpMethod.GET);
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
