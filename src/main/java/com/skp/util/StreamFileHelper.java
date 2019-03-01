package com.skp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class StreamFileHelper {
//	private static final Logger logger = LoggerFactory.getLogger(StreamFileHelper.class);

	public static Stream<String> getFile(String relativePath) {
		if (existFromPath(relativePath))
			return getFileFromPath(relativePath);
		return getFileFromResource(relativePath);
	}

	public static Stream<String> getFileFromPath(String relativePath) {
		try {
			String workDir = System.getProperty("user.dir");
	        String path = workDir + "/" + relativePath;
			return Files.lines(Paths.get(path));
		} catch (IOException e) {
//			e.printStackTrace();
		}
		return Stream.empty();
	}

	public static Stream<String> getFileFromResource(String relativePath) {
		try {
			InputStream is = StreamFileHelper.class.getClassLoader().getResourceAsStream(relativePath);
			return new BufferedReader(new InputStreamReader(is, "UTF-8")).lines();
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
		}
		return Stream.empty();
	}

	/*
    public static InputStream getInputStream(String relativePath) {
        String workDir = System.getProperty("user.dir");
        String path = workDir + "/" + relativePath;
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(new File(path));
//          logger.debug("getConfigInputStream config from file: " + path);
            return inputStream;
        } catch (FileNotFoundException e) {
//          logger.debug("getConfigInputStream config from file: " + path);
        }

        inputStream = FileStreamHelper.class.getClassLoader().getResourceAsStream(relativePath);
//        	logger.debug("getConfigInputStream config from resource: " + file);
        return inputStream;
    }

    public static InputStream getInputStreamFromPath(String relativePath) {
        String workDir = System.getProperty("user.dir");
        String path = workDir + "/" + relativePath;
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(new File(path));
            return inputStream;
        } catch (FileNotFoundException e) {
        	inputStream = null;
        }
        return inputStream;
    }

    public static InputStream getInputStreamFromResource(String relativePath) {
    	InputStream inputStream = null;
        inputStream = FileStreamHelper.class.getClassLoader().getResourceAsStream(relativePath);
        return inputStream;
    }

    public static String readInputStream(InputStream is) {
    	if (is == null)
    		return null;

        StringBuilder textBuilder = new StringBuilder();
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is));
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        } catch (IOException e) {
        	logger.error(e.toString());
        }
        return textBuilder.toString();
    }

    // Process by line-by-line callback
    public static void processFileFromResource(String relativePath, LineReadCallback callback) {
    	InputStream is = getInputStream(relativePath);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                callback.processLine(line);
            }
        } catch (IOException e) {
        	logger.error(e.toString());
        }
    }

    public interface LineReadCallback {
    	void processLine(String line);
    }

	public static List<String> getFileLineList(String relativePath) {
    	InputStream is = getInputStream(relativePath);
    	ArrayList<String> lineList = new ArrayList<>();

    	try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                lineList.add(line);
            }
        } catch (IOException e) {
            logger.error(e.toString());
        }
    	return lineList;
	}

	public static List<String> getFileLineListFromPath(String relativePath) {
    	InputStream is = getInputStreamFromPath(relativePath);
    	ArrayList<String> lineList = new ArrayList<>();

    	try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                lineList.add(line);
            }
        } catch (IOException e) {
            logger.error(e.toString());
        }
    	return lineList;
	}

	public static List<String> getFileLineListFromResource(String relativePath) {
    	InputStream is = getInputStreamFromResource(relativePath);
    	ArrayList<String> lineList = new ArrayList<>();

    	try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                lineList.add(line);
            }
        } catch (IOException e) {
            logger.error(e.toString());
        }
    	return lineList;
	} */

	public static boolean existFromPath(String relativePath) {
		String workDir = System.getProperty("user.dir");
        String path = workDir + "/" + relativePath;
		return (Files.exists(Paths.get(path)));
	}
}
