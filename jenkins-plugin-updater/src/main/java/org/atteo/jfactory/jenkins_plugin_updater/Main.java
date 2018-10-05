package org.atteo.jfactory.jenkins_plugin_updater;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
    public static final String JENKINS_UPDATE_CENTER_URL = "https://updates.jenkins-ci.org/current/update-center.actual.json";
    public static final String PLUGINS_VERSION_FILE = "plugins.txt";

    public static void main(String[] args) throws IOException {
        System.out.println("Downloading: " + JENKINS_UPDATE_CENTER_URL);
        List<Object> entries = getEntries();
        generateOutputFile(entries);
        System.out.println("Plugins updated");
    }

    private static List<Object> getEntries() throws IOException {
        List<Object> entries = new ArrayList<>();
        DocumentContext json = downloadUpdateCenterJson();
        try (Stream<String> stream = Files.lines(Paths.get(PLUGINS_VERSION_FILE))) {
            stream.forEach(line -> {
				if (line.startsWith("#")) {
					entries.add(line);
					return;
				}
                String[] splitted = line.split(":");

                if (splitted.length != 2) {
					entries.add(line);
                    return;
                }

                String pluginName = splitted[0];
                String oldPluginVersion = splitted[1];

                String path = "$.plugins['" + pluginName + "'].version";
                String pluginVersion = json.read(path, String.class);

                entries.add(new Plugin(pluginName, pluginVersion));
            });
        }
        return entries;
    }

    private static DocumentContext downloadUpdateCenterJson() throws IOException {
        String out = new Scanner(new URL(JENKINS_UPDATE_CENTER_URL).openStream(), "UTF-8").useDelimiter("\\A").next();
        return JsonPath.parse(out);
    }

    private static void generateOutputFile(List<Object> entries) throws IOException {
        Path path = Paths.get(PLUGINS_VERSION_FILE);
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			entries.forEach(entry -> {
				try {
					if (entry instanceof Plugin) {
						Plugin plugin = (Plugin) entry;
						writer.write(plugin.getName() + ":" + plugin.getVersion() + "\n");
					} else {
						writer.write(entry.toString());
						writer.write("\n");
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}
}
