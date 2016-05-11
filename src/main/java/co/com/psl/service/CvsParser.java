package co.com.psl.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import co.com.psl.model.GithubRepository;

@Component
public class CvsParser {

	@Autowired
	private ResourceLoader resourceLoader;
	
	public static final String CSV_SEPARATOR =",";
	
	public static final String CSV_FILE = "classpath:repos.csv";
	
	public String[] loadCsvFile() throws IOException {
		Resource resource = resourceLoader.getResource(CSV_FILE);
		InputStream inputStream = resource.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer);
		return writer.toString().split("\\r?\\n");
	};
	
	public void generateCvsFile(String fileName, ArrayList<GithubRepository> reposInformation) throws IOException{
		FileWriter writer = new FileWriter(fileName + ".csv");
		writer.append(generateReportHeader());
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
	
	public String generateReportHeader(){
		return "Repo Name, Number of Lines, Number of Files, Year Modifications \n";
	}
}
