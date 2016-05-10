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

import co.com.psl.exceptions.ReportNotReadyException;

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
	public static final String GITHUB_API_TREES_RECURSIVE = "/git/trees/";
	public static final String GITHUB_API_ADDITIONS_PER_WEEK = "/stats/code_frequency";


	public JsonArray getContributorsByRepo(String repository) throws IOException, ReportNotReadyException {
		StringBuilder getContributorsUrl = new StringBuilder()
			.append(GITHUB_API)
			.append(repository)
			.append(GITHUB_API_CONTRIBUTORS)
			.append(this.propertyResolver.getProperty("GITHUB_ACCESS_TOKEN"))
			.append("&")
			.append(this.propertyResolver.getProperty("GITHUB_CLIENT_ID"))
			.append("&")
			.append(this.propertyResolver.getProperty("GITHUB_CLIENT_SECRET"));
		String httpResponse = makeHttpRequest(getContributorsUrl.toString(), HttpMethod.GET);
		return parser.parse(httpResponse).getAsJsonArray();
	}
	
	public String getDefaultBranch(String repository) throws IOException, ReportNotReadyException{
		StringBuilder defaultBranchRequest = new StringBuilder()
			.append(GITHUB_API)
			.append(repository)
			.append("?access_token=")
			.append(this.propertyResolver.getProperty("GITHUB_ACCESS_TOKEN"));
		String httpResponse = makeHttpRequest(defaultBranchRequest.toString(), HttpMethod.GET);
		return parser.parse(httpResponse).getAsJsonObject().get("default_branch").getAsString();
	}
	
	public JsonArray getNumberOfFilesByRepository(String repository) throws IOException, ReportNotReadyException{
		StringBuilder getTreeFilesUrl = new StringBuilder()
			.append(GITHUB_API)
			.append(repository)
			.append(GITHUB_API_TREES_RECURSIVE)
			.append(getDefaultBranch(repository))
			.append("?recursive=1&access_token=")
			.append(this.propertyResolver.getProperty("GITHUB_ACCESS_TOKEN"))
			.append("&")
			.append(this.propertyResolver.getProperty("GITHUB_CLIENT_ID"))
			.append("&")
			.append(this.propertyResolver.getProperty("GITHUB_CLIENT_SECRET"));
		String httpResponse = makeHttpRequest(getTreeFilesUrl.toString(), HttpMethod.GET);
		return parser.parse(httpResponse).getAsJsonObject().get("tree").getAsJsonArray();
	}
	
	public JsonArray getNumberOfLinesModificationsFromDate(String repository, String date, String branch) throws IOException, ReportNotReadyException{
		StringBuilder commitsUrl = new StringBuilder()
			.append(GITHUB_API)
			.append(repository)
			.append(GITHUB_API_ADDITIONS_PER_WEEK);
		String httpResponse = makeHttpRequest(commitsUrl.toString(), HttpMethod.GET);
		return parser.parse(httpResponse).getAsJsonArray();	
	}
	
	public String makeHttpRequest(String url, HttpMethod method) throws IOException, ReportNotReadyException{
		URL urlToRequest = new URL(url);
		HttpURLConnection httpConnection = (HttpURLConnection) urlToRequest.openConnection();
		httpConnection.setRequestMethod(method.toString());
		int statusCode = httpConnection.getResponseCode();
		if (statusCode == 202) {
			throw new ReportNotReadyException();
		}else if(statusCode == 204){
			return "[]";
		}
		InputStream inputStream = httpConnection.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer);
		return writer.toString();
	}
}
