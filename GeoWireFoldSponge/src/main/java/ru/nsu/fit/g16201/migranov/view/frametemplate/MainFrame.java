package ru.nsu.fit.g16201.migranov.view.frametemplate;

import ru.nsu.fit.g16201.migranov.view.StatusTitleListener;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;

import javax.swing.*;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;


public class MainFrame extends JFrame {
	private JMenuBar menuBar;
	protected JToolBar toolBar;

	private MainFrame()
	{
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch(Exception e)
		{
		}
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		toolBar = new JToolBar("Main toolbar");
		toolBar.setRollover(true);
		add(toolBar, BorderLayout.PAGE_START);
	}

	public MainFrame(int x, int y, String title)
	{
		this();
		setSize(x, y);
		setLocationByPlatform(true);
		setTitle(title);
	}

	private JMenuItem createMenuItem(String title, String tooltip, int mnemonic, String icon, String actionMethod) throws SecurityException, NoSuchMethodException
	{
		JMenuItem item = new JMenuItem(title);
		item.setMnemonic(mnemonic);
		item.setToolTipText(tooltip);
		if(icon != null)
			item.setIcon(new ImageIcon(getClass().getResource("resources/"+icon), title));
		final Method method = getClass().getMethod(actionMethod);
		item.addActionListener(evt -> {
			try {
				method.invoke(MainFrame.this);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return item;
	}

	private JMenu createSubMenu(String title, int mnemonic)
	{
		JMenu menu = new JMenu(title);
		menu.setMnemonic(mnemonic);
		return menu;
	}

	protected void addSubMenu(String title, int mnemonic)
	{
		MenuElement element = getParentMenuElement(title);
		if(element == null)
			throw new InvalidParameterException("Menu path not found: "+title);
		JMenu subMenu = createSubMenu(getMenuPathName(title), mnemonic);
		if(element instanceof JMenuBar)
			((JMenuBar)element).add(subMenu);
		else if(element instanceof JMenu)
			((JMenu)element).add(subMenu);
		else if(element instanceof JPopupMenu)
			((JPopupMenu)element).add(subMenu);
		else 
			throw new InvalidParameterException("Invalid menu path: "+title);
	}

	private void addMenuItem(String title, String tooltip, int mnemonic, String icon, String actionMethod, JLabel label) throws SecurityException, NoSuchMethodException
	{
		MenuElement element = getParentMenuElement(title);
		if(element == null)
			throw new InvalidParameterException("Menu path not found: "+title);
		JMenuItem item = createMenuItem(getMenuPathName(title), tooltip, mnemonic, icon, actionMethod);
		if(label != null) {
			item.addMouseListener(new StatusTitleListener(label));
		}
		if(element instanceof JMenu)
			((JMenu)element).add(item);
		else if(element instanceof JPopupMenu)
			((JPopupMenu)element).add(item);
		else 
			throw new InvalidParameterException("Invalid menu path: "+title);
	}

	protected String getMenuPathName(String menuPath)
	{
		int pos = menuPath.lastIndexOf('/');
		if(pos > 0)
			return menuPath.substring(pos+1);
		else
			return menuPath;
	}

	protected MenuElement getParentMenuElement(String menuPath)
	{
		int pos = menuPath.lastIndexOf('/');
		if(pos > 0)
			return getMenuElement(menuPath.substring(0, pos));
		else
			return menuBar;
	}

	private MenuElement getMenuElement(String menuPath)
	{
		MenuElement element = menuBar;
		for(String pathElement: menuPath.split("/"))
		{
			MenuElement newElement = null;
			for(MenuElement subElement: element.getSubElements())
			{
				if((subElement instanceof JMenu && ((JMenu)subElement).getText().equals(pathElement))
						|| (subElement instanceof JMenuItem && ((JMenuItem)subElement).getText().equals(pathElement)))
				{
					if(subElement.getSubElements().length==1 && subElement.getSubElements()[0] instanceof JPopupMenu)
						newElement = subElement.getSubElements()[0];
					else
						newElement = subElement;
					break;
				}
			}
			if(newElement == null) return null;
			element = newElement;
		}
		return element;
	}


	protected File getSaveFileName(String extension, String description)
	{
		return FileUtils.getSaveFileName(this, extension, description);
	}

	protected File getOpenFileName(String extension, String description)
	{
		return FileUtils.getOpenFileName(this, extension, description);
	}
}
