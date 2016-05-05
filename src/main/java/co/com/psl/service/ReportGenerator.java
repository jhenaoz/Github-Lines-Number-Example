package co.com.psl.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import co.com.psl.model.GithubRepository;
import co.com.psl.request.GithubWrapper;

@Component
public class ReportGenerator {

	
	public static final String CSV_SEPARATOR = ",";
	@Autowired
	private CvsParser cvsParser;

	@Autowired
	private GithubWrapper github;
	
	private ArrayList<GithubRepository> reposInformation = new ArrayList<GithubRepository>();

	@PostConstruct
	public void generateReposStatisticsReport() throws IOException {
		System.out.println("Generating Report....");
		String[] repos = cvsParser.loadCsvFile();
		for (String repo : repos) {
			String repoName = getRepoNameFromCvsLine(repo);
			int lines = getLinesNumberByRepo(repoName);
			reposInformation.add(new GithubRepository(repoName, lines));
		}
		exportReportAsCsv("Repositories Report");
		System.exit(0);
	}
	
	private void exportReportAsCsv(String fileName) throws IOException {
		FileWriter writer = new FileWriter(fileName + ".csv");
		
		for (GithubRepository githubRepository : reposInformation) {
			writer.append(githubRepository.getName());
			writer.append(CSV_SEPARATOR);
			writer.append(String.valueOf(githubRepository.getCodeLines()));
			writer.append("\n");
			
		}
		writer.flush();
		writer.close();
	}

	public String getRepoNameFromCvsLine(String cvsLine){
		String[] cvsElements = cvsLine.split(","); 
		return cvsElements[0];
	}

	public int getLinesNumberByRepo(String repo) throws IOException {
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

}
