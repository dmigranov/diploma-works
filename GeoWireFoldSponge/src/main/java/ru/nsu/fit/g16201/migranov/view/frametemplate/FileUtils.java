package ru.nsu.fit.g16201.migranov.view.frametemplate;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

public class FileUtils {
	private static File dataDirectory = null;

	public static File getDataDirectory()
	{
		if(dataDirectory == null)
		{
			try
			{
				String path = URLDecoder.decode(MainFrame.class.getProtectionDomain().getCodeSource().getLocation().getFile(), Charset.defaultCharset().toString());
				dataDirectory = new File(path).getParentFile();
			}
			catch (UnsupportedEncodingException e)
			{
				dataDirectory = new File(".");
			}
			if(dataDirectory == null || !dataDirectory.exists()) dataDirectory = new File(".");
			for(File f: dataDirectory.listFiles())
			{
				if(f.isDirectory() && f.getName().endsWith("_Data"))
				{
					dataDirectory = f;
					break;
				}
			}
		}
		return dataDirectory;
	}

	public static File getSaveFileName(JFrame parent, String extension, String description)
	{
		JFileChooser fileChooser = new JFileChooser();
		FileFilter filter = new ExtensionFileFilter(extension, description);
		fileChooser.addChoosableFileFilter(filter);
		fileChooser.setCurrentDirectory(getDataDirectory());
		if(fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION)
		{
			File f = fileChooser.getSelectedFile();
			if(!f.getName().contains("."))
				f = new File(f.getParent(), f.getName()+"."+extension);
			return f;
		}
		return null;
	}

	public static File getOpenFileName(JFrame parent, String extension, String description)
	{
		JFileChooser fileChooser = new JFileChooser();
		FileFilter filter = new ExtensionFileFilter(extension, description);
		fileChooser.addChoosableFileFilter(filter);
		fileChooser.setCurrentDirectory(getDataDirectory());
		if(fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
		{
			File f = fileChooser.getSelectedFile();
			if(!f.getName().contains("."))
				f = new File(f.getParent(), f.getName()+"."+extension);
			return f;
		}
		return null;
	}

	public static File getOpenFileName(JFrame parent)
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(getDataDirectory());
		if(fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
		{
			File f = fileChooser.getSelectedFile();

			return f;
		}
		return null;
	}
}
