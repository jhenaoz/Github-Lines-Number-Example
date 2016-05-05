package co.com.psl.model;

public class GithubRepository {

	private String name;
	private int codeLines;
	private int files;
	
	public GithubRepository(){};
	
	public GithubRepository(String name, int quantityOfLines, int quantityOfFiles) {
		this.name = name;
		this.codeLines = quantityOfLines;
		this.files = quantityOfFiles;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getCodeLines() {
		return codeLines;
	}
	
	public void setCodeLines(int codeLines) {
		this.codeLines = codeLines;
	}

	public int getFiles() {
		return files;
	}

	public void setFiles(int files) {
		this.files = files;
	}
}
