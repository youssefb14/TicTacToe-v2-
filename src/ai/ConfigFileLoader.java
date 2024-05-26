package ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ConfigFileLoader {

	/**
	 * Config File format: level:hiddenLayerSize:learningRate:numberOfhiddenLayers
	 * @param f
	 */
	public void loadConfigFile(String name) {
		try {
			File f = new File(name) ;
			this.mapConfig = new HashMap<>();
			if(f.exists() && f.isFile()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				String s = "";
				while( (s = br.readLine()) != null ) {
					String[] t = s.split(":");
					if ( t.length == 4 ) {
						String level = t[0];
						int hiddenLayerSize = Integer.parseInt(t[1]);
						double learningRate = Double.parseDouble(t[2]);
						int numberOfhiddenLayers = Integer.parseInt(t[3]);
						//
						Config c = new Config(level, hiddenLayerSize, numberOfhiddenLayers, learningRate);
						mapConfig.put(level, c);
					}
				}
				br.close();
			}
		}
		catch (Exception e) {
			System.out.println("Config.loadConfigFile()");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public Config get(String level) {
		try {
			if (mapConfig != null && mapConfig.containsKey(level) )
				return mapConfig.get(level);
		}
		catch (Exception e) {
			System.out.println("ConfigFileLoader.get()");
			e.printStackTrace();
			System.exit(-1);
		}
		return null ;
	}

	@Override
	public String toString() {
		return "ConfigFileLoader [mapConfig=" + mapConfig + "]";
	}

	private HashMap<String, Config> mapConfig ;

	public void saveConfigFile(Config c) {

		 try {

			 File f = new File("resources/config.txt");
			 if (f.exists() && f.isFile()) {
				 mapConfig.put(c.level, c);
				 java.io.PrintWriter pw = new java.io.PrintWriter(f);
				 for (String key : mapConfig.keySet()) {
					 Config c2 = mapConfig.get(key);
					 pw.println(c2.level + ":" + c2.hiddenLayerSize + ":" + c2.learningRate + ":"
					 + c2.numberOfhiddenLayers);
				 }
				 pw.close();
			 }
		 } catch (Exception e) {
			 System.out.println("ConfigFileLoader.saveConfigFile()");
			 e.printStackTrace();
			 System.exit(-1);
		 }
	 }
}
