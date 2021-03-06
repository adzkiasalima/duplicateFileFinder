import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;

public class MainApp {
    public static void main(String[] args) {

        String fileName = args[0];

        Map<String, List<File>> files = new HashMap<>();

        File directory = new File(fileName);

        findDuplicatedFiles(files, directory);

        // Get the biggest number of files that have same content
        String maxKey = files.entrySet().stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().size()))
                .orElseThrow().getKey();

        String sampleFile = files.get(maxKey).get(0).getAbsolutePath();

        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(sampleFile)));
            System.out.println(content + " " + files.get(maxKey).size());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static final MessageDigest messageDigest;
    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public static Map<String, List<File>> findDuplicatedFiles(Map<String, List<File>> lists, File directory) {

        File[] files = directory.listFiles();

        if (files == null) throw new RuntimeException("directory is empty");

        for (File child : files) {
            if (child.isDirectory()) {
                findDuplicatedFiles(lists, child);
            } else {
                try {
                    FileInputStream fileInput = new FileInputStream(child);
                    byte[] fileData = new byte[(int) child.length()];
                    fileInput.close();

                    String uniqueFileHash = new BigInteger(1, messageDigest.digest(fileData)).toString(16);
                    List<File> list = lists.get(uniqueFileHash);
                    if (list == null) {
                        list = new LinkedList<>();
                        lists.put(uniqueFileHash, list);
                    }
                    list.add(child);
                } catch (IOException e) {
                    throw new RuntimeException("cannot read file " + child.getAbsolutePath(), e);
                }
            }
        }
        return lists;
    }

}
