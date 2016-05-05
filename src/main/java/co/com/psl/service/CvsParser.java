package co.com.psl.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class CvsParser {

	@Autowired
	private ResourceLoader resourceLoader;
	
	public static final String CSV_FILE = "classpath:repos.csv";
	
	public String[] loadCsvFile() throws IOException {
		Resource resource = resourceLoader.getResource(CSV_FILE);
		InputStream inputStream = resource.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer);
		return writer.toString().split("\\r?\\n");
	};
}
