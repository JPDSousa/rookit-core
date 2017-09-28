package org.rookit.core.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

final class ConfigUtils {
	
	private static Gson gson;
	
	private static final Gson buildGson() {
		final GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		return builder.create();
	}
	
	static Config fromPath(Path path) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		if(gson == null) {
			gson = buildGson();
		}
		return gson.fromJson(new FileReader(path.toFile()), Config.class);
	}
	
	static void toPath(Config config, Path path) throws IOException {
		if(gson == null) {
			gson = buildGson();
		}
		Files.write(path, gson.toJson(config).getBytes(), StandardOpenOption.WRITE);
	}
	
	static <T> T getOrDefault(T get, T def) {
		return get != null ? get : def;
	}
	
	static <T, E> T getOrDefault(E get, T def, Function<E, T> mapper) {
		return get != null ? mapper.apply(get) : def;
	}

}
