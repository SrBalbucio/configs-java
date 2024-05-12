package balbucio.configs;

import balbucio.throwable.Throwable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Configs {

    private static LoaderOptions loaderOptions = new LoaderOptions();
    private static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls().create();

    public static <T> T loadObjectFromYamlFile(File file, Class<T> clazz) throws Exception {
        TagInspector taginspector =
                tag -> tag.getClassName().equals(clazz.getName());
        loaderOptions.setTagInspector(taginspector);
        Yaml yml = new Yaml(new Constructor(clazz, loaderOptions));
        return yml.load(Files.newInputStream(file.toPath()));
    }

    public static void saveObjectInYamlFile(File file, Object object) throws Exception{
        PrintWriter writer = new PrintWriter(file);
        Yaml yml = new Yaml(new Constructor(object.getClass(), new LoaderOptions()));
        yml.dump(object, writer);
    }

    public static void saveObjectInJsonFile(File file, Object obj) throws Exception{
        FileWriter writer = new FileWriter(file);
        writer.append(gson.toJson(obj));
        writer.flush();
        writer.close();
    }

    public static <T> T loadObjectFromJsonFile(File file, Class<T> clazz) throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return gson.fromJson(reader, clazz);
    }

    public static <T> T getObjectInJarEntry(JarFile jarFile, String path, Class<T> clazz) throws IOException {
        JarEntry entry = jarFile.getJarEntry(path);
        if(entry != null && !entry.isDirectory()) {
            return gson.fromJson(new InputStreamReader(jarFile.getInputStream(entry)), clazz);
        }
        return null;
    }

    public static YamlConfiguration copyOrLoadYamlConfiguration(File file, String classpathFile){
        if(!file.exists()){
            Throwable.silently(() -> Files.copy(Configs.class.getResourceAsStream(classpathFile), file.toPath()));
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static JSONObject loadJSONObject(File file) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return new JSONObject(reader);
    }

    public static void saveJSONObject(File file, JSONObject json) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.append(json.toString());
        writer.flush();
        writer.close();
    }
}
