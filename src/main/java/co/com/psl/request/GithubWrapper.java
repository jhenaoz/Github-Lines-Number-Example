package co.com.psl.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

@Component
public class GithubWrapper {

	public static final String GITHUB_API = "https://api.github.com/repos/";
	public static final String GITHUB_ACCESS_TOKEN = "f65df79a61f82066f479683da625b9ed6ef0156a";

	public JsonArray GetContributorsByRepo(String repo) {
		String urlString = GITHUB_API + repo + "/stats/contributors?access_token=" + GITHUB_ACCESS_TOKEN;
		URL url;
		try {
			url = new URL(urlString);
			URLConnection conn = url.openConnection();
			InputStream inputStream = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer);
			
			JsonParser parser = new JsonParser();
			return parser.parse(writer.toString()).getAsJsonArray();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		}
		return null;
	}
}
