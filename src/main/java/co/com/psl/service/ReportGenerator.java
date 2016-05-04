package co.com.psl.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.com.psl.request.GithubWrapper;

@Component
public class ReportGenerator {

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	GithubWrapper github;

	@PostConstruct
	public void generateReposStatisticsReport() {
		System.out.println("generating report....");
		String json = loadReposJsonFile();
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
		JsonArray jsonArray = jsonObject.get("repos").getAsJsonArray();
		for (JsonElement jsonElement : jsonArray) {
			System.out.println(getLinesNumberByRepo(jsonElement.getAsJsonObject().get("url").getAsString()));
		}
		System.exit(0);
	}

	public int getLinesNumberByRepo(String repo) {
		int codeLines = 0;
		JsonArray contributors = github.GetContributorsByRepo(repo);
		for (JsonElement contributor : contributors) {
			JsonArray contributedWeeks = contributor.getAsJsonObject().get("weeks").getAsJsonArray();
			for (JsonElement week : contributedWeeks) {
				codeLines += week.getAsJsonObject().get("a").getAsInt();
				codeLines -= week.getAsJsonObject().get("d").getAsInt();
			}
		}
		return codeLines;
	}

	public String loadReposJsonFile() {
		Resource resource = resourceLoader.getResource("classpath:repos.json");
		try {
			InputStream inputStream = resource.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer);
			return writer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
