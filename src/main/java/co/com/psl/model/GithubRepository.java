package co.com.psl.model;

public class GithubRepository {

	private String name;
	private int numberOfLines;
	private int numberOfFiles;
	private int numberOfModifications;
	
	public GithubRepository(){};
	
	public GithubRepository(String name, int numberOfLines, int numberOfFiles, int numberOfModifications) {
		this.name = name;
		this.numberOfLines = numberOfLines;
		this.numberOfFiles = numberOfFiles;
		this.numberOfModifications = numberOfModifications;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public int getNumberOfLines() {
		return numberOfLines;
	}

	public void setNumberOfLines(int numberOfLines) {
		this.numberOfLines = numberOfLines;
	}

	public int getNumberOfFiles() {
		return numberOfFiles;
	}

	public void setNumberOfFiles(int numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}

	public int getNumberOfModifications() {
		return numberOfModifications;
	}

	public void setNumberOfModifications(int numberOfModifications) {
		this.numberOfModifications = numberOfModifications;
	}
	

}
