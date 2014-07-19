package inventoryperipheral.peripheral;

import inventoryperipheral.util.LuaUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import dan200.computercraft.api.filesystem.IMount;

public class LuaMount implements IMount {

	private static String lua_path;
	private static String extracted_lua_path;

	public static void initialize() {
		final String resource_path = "/assets/inventoryperipheral";
		lua_path = String.format("%s/lua", resource_path);
		extracted_lua_path = String.format("mods/InventoryPeripheral/%s/lua", LuaUtil.getModContainer().getVersion());

		setupLuaFiles();
	}

	public static boolean setupLuaFiles() {
		File modFile = LuaUtil.getModContainer().getSource();
		File baseFile = LuaUtil.getBase();

		if (modFile.isDirectory()) {
			File srcFile = new File(modFile, lua_path);
			File destFile = new File(baseFile, extracted_lua_path);
			if (destFile.exists()) {
				return false;
			}
			try {
				LuaUtil.copy(srcFile, destFile);
			} catch (IOException e) {
			}
		} else {
			File destFile = new File(baseFile, extracted_lua_path);
			if (destFile.exists()) {
				return false;
			}
			LuaUtil.extractZipToLocation(modFile, lua_path, extracted_lua_path);
		}
		return true;
	}

	@Override
	public boolean exists(String path) throws IOException {
		File file = new File(new File(extracted_lua_path), path);
		return file.exists();
	}

	@Override
	public boolean isDirectory(String path) throws IOException {
		File file = new File(new File(extracted_lua_path), path);
		return file.isDirectory();
	}

	@Override
	public void list(String path, List<String> contents) throws IOException {
		File directory = new File(new File(extracted_lua_path), path);
		for (File file : directory.listFiles()) {
			contents.add(file.getName());
		}
	}

	@Override
	public long getSize(String path) throws IOException {
		File file = new File(new File(extracted_lua_path), path);
		return file.length();
	}

	@Override
	public InputStream openForRead(String path) throws IOException {
		File file = new File(new File(extracted_lua_path), path);
		return new FileInputStream(file);
	}

}