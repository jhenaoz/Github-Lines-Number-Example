package co.com.psl.model;

public class GithubRepository {

	private String name;
	private int codeLines;
	
	public GithubRepository(){};
	
	public GithubRepository(String repoName, int lines) {
		this.name = repoName;
		this.codeLines = lines;
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
}
