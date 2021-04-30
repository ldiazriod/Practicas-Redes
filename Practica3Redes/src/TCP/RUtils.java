package TCP;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class RUtils {
	public static String getStatisticsRScriptCode() throws URISyntaxException, IOException {
		URI rScript = RUtils.class.getClassLoader().getResource("statistics.R").toURI();
		Path inputScript = Paths.get(rScript);
		return Files.lines(inputScript).collect(Collectors.joining());
	}
}
