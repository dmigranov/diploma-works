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
	private static final long serialVersionUID = 1L;
	private JMenuBar menuBar;
	protected JToolBar toolBar;

	public MainFrame()
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

	public JMenuItem createMenuItem(String title, String tooltip, int mnemonic, String icon, String actionMethod) throws SecurityException, NoSuchMethodException
	{
		JMenuItem item = new JMenuItem(title);
		item.setMnemonic(mnemonic);
		item.setToolTipText(tooltip);
		if(icon != null)
			item.setIcon(new ImageIcon(getClass().getResource("resources/"+icon), title));
		final Method method = getClass().getMethod(actionMethod);
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					method.invoke(MainFrame.this);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		return item;
	}

	public JMenuItem createMenuItem(String title, String tooltip, int mnemonic, String actionMethod) throws SecurityException, NoSuchMethodException
	{
		return createMenuItem(title, tooltip, mnemonic, null, actionMethod);
	}

	/**
	 * Creates submenu and returns it
	 * @param title - submenu title
	 * @param mnemonic - mnemonic key to activate submenu via keyboard
	 * @return created submenu
	 */
	public JMenu createSubMenu(String title, int mnemonic)
	{
		JMenu menu = new JMenu(title);
		menu.setMnemonic(mnemonic);
		return menu;
	}

	public void addSubMenu(String title, int mnemonic)
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

	public void addMenuItem(String title, String tooltip, int mnemonic, String icon, String actionMethod, JLabel label) throws SecurityException, NoSuchMethodException
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

	public void addMenuItem(String title, String tooltip, int mnemonic, String actionMethod, JLabel label) throws SecurityException, NoSuchMethodException
	{
		addMenuItem(title, tooltip, mnemonic, null, actionMethod, label);
	}

	public void addMenuSeparator(String title)
	{
		MenuElement element = getMenuElement(title);
		if(element == null)
			throw new InvalidParameterException("Menu path not found: "+title);
		if(element instanceof JMenu)
			((JMenu)element).addSeparator();
		else if(element instanceof JPopupMenu)
			((JPopupMenu)element).addSeparator();
		else
			throw new InvalidParameterException("Invalid menu path: "+title);
	}

	protected String getMenuPathName(String menuPath) {
		int pos = menuPath.lastIndexOf('/');
		if (pos > 0)
			return menuPath.substring(pos + 1);
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

	public MenuElement getMenuElement(String menuPath)
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

	public JButton createToolBarButton(JMenuItem item)
	{
		JButton button = new JButton(item.getIcon());
		for(ActionListener listener: item.getActionListeners())
			button.addActionListener(listener);
		button.setToolTipText(item.getToolTipText());
		return button;
	}

	public JButton createToolBarButton(String menuPath)
	{
		JMenuItem item = (JMenuItem)getMenuElement(menuPath);
		if(item == null) 
			throw new InvalidParameterException("Menu path not found: "+menuPath);
		return createToolBarButton(item);
	}

	public void addToolBarButton(String menuPath, JLabel label)
	{
		JButton button = createToolBarButton(menuPath);
		if(label != null)
			button.addMouseListener(new StatusTitleListener(label));
		toolBar.add(button);
	}

	public File getSaveFileName(String extension, String description)
	{
		return FileUtils.getSaveFileName(this, extension, description);
	}

	public File getOpenFileName(String extension, String description)
	{
		return FileUtils.getOpenFileName(this, extension, description);
	}
}
