package co.com.psl.service;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
	public static final int MONTHS_TO_SEARCH = 12;
	@Autowired
	private CvsParser cvsParser;

	@Autowired
	private GithubWrapper github;
	
	private ArrayList<GithubRepository> reposInformation = new ArrayList<GithubRepository>();

	@PostConstruct
	public void generateReposStatisticsReport() throws IOException {
		String[] repos = cvsParser.loadCsvFile();
		for (String repo : repos) {
			String repoName = getRepoNameFromCvsLine(repo);
			int numberOfLines = getLinesNumberByRepo(repoName);
			int numberOfFiles = getFilesByRepository(repoName);
			int numberOfModifiedLines = getModifiedLinesInTheLastMonths(repoName, MONTHS_TO_SEARCH);
			reposInformation.add(new GithubRepository(repoName, numberOfLines, numberOfFiles, numberOfModifiedLines ));
		}
		exportReportAsCsv("Repositories Report");
		System.exit(0);
	}
	
	private int getFilesByRepository(String repositoryName) throws IOException {
		JsonArray files = github.getNumberOfFilesByRepository(repositoryName);
		return files.size();
	}

	private void exportReportAsCsv(String fileName) throws IOException {
		FileWriter writer = new FileWriter(fileName + ".csv");
		for (GithubRepository githubRepository : reposInformation) {
			writer.append(githubRepository.getName());
			writer.append(CSV_SEPARATOR);
			writer.append(String.valueOf(githubRepository.getNumberOfLines()));
			writer.append(CSV_SEPARATOR);
			writer.append(String.valueOf(githubRepository.getNumberOfFiles()));
			writer.append(CSV_SEPARATOR);
			writer.append(String.valueOf(githubRepository.getNumberOfModifications()));
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
		JsonArray contributors = github.getContributorsByRepo(repo);
		for (JsonElement contributor : contributors) {
			JsonArray contributedWeeks = contributor.getAsJsonObject().get("weeks").getAsJsonArray();
			for (JsonElement week : contributedWeeks) {
				codeLines += week.getAsJsonObject().get("a").getAsInt();
				codeLines -= week.getAsJsonObject().get("d").getAsInt();
			}
		}
		return codeLines;
	}
	
	public int getModifiedLinesInTheLastMonths(String repo, int months) throws IOException{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONDAY, (months*-1));
		String beginingDateForSearch = convertDateToGithubFormat(calendar);
		JsonArray weeksOfWork = github.getNumberOfLinesModificationsFromDate(repo, beginingDateForSearch, "master");
		int weeksToCalculateModifications = months*4; // 4 weeks per month;
		int modifiedLines = 0;
		if (weeksToCalculateModifications > weeksOfWork.size()) {
			for (JsonElement week : weeksOfWork) {
				modifiedLines += Math.abs(week.getAsJsonArray().get(1).getAsInt());
				modifiedLines += Math.abs(week.getAsJsonArray().get(2).getAsInt());
			}
		}else{
			for (int i = 0; i < weeksToCalculateModifications; i++) {
				JsonElement week = weeksOfWork.get(i);
				modifiedLines += Math.abs(week.getAsJsonArray().get(1).getAsInt());
				modifiedLines += Math.abs(week.getAsJsonArray().get(2).getAsInt());
			}
		}

		return modifiedLines;
	}
	
	public String convertDateToGithubFormat(Calendar calendar){
		DateFormat dateFormatInIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		dateFormatInIso8601.setTimeZone(calendar.getTimeZone());
		return  dateFormatInIso8601.format(calendar.getTime());
	}

}
