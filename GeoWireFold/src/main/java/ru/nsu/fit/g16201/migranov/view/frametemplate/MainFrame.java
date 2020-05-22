package ru.nsu.fit.g16201.migranov.view.frametemplate;

import ru.nsu.fit.g16201.migranov.view.StatusTitleListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;


public class MainFrame extends JFrame {
	private JMenuBar menuBar;
	private JToolBar toolBar;
	protected List<AbstractButton> deactivatedButtons = new ArrayList<>();

	private JLabel statusLabel = new JLabel("");

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

		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusPanel.add(statusLabel);
		add(statusPanel, BorderLayout.SOUTH);
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

	private JMenu createSubMenu(String title, int mnemonic)
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

	public void addMenuAndToolBarButton(String path, String tooltip, int mnemonic, String icon, String actionMethod, boolean isDeactivated) throws NoSuchMethodException
	{
		MenuElement element = getParentMenuElement(path);
		if(element == null)
			throw new InvalidParameterException("Menu path not found: " + path);
		String title = getMenuPathName(path);
		JMenuItem item = new JMenuItem(title);
		item.setMnemonic(mnemonic);
		item.setToolTipText(tooltip);
		item.addMouseListener(new StatusTitleListener(statusLabel));
		final Method method = getClass().getMethod(actionMethod);
		item.addActionListener(evt -> {
			try {
				method.invoke(this);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		if(element instanceof JMenu)
			((JMenu)element).add(item);
		else if(element instanceof JPopupMenu)
			((JPopupMenu)element).add(item);
		else
			throw new InvalidParameterException("Invalid menu path: " + path);

		JButton button = new JButton();
		if(icon != null)
			button.setIcon(new ImageIcon(getClass().getResource("/"+icon), title));
		for(ActionListener listener: item.getActionListeners())
			button.addActionListener(listener);
		button.setToolTipText(tooltip);
		button.addMouseListener(new StatusTitleListener(statusLabel));
		toolBar.add(button);
		if(isDeactivated)
		{
			item.setEnabled(false);
			button.setEnabled(false);
			deactivatedButtons.add(item);
			deactivatedButtons.add(button);
		}
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


	public File getSaveFileName(String extension, String description)
	{
		return FileUtils.getSaveFileName(this, extension, description);
	}

	public File getOpenFileName(String extension, String description)
	{
		return FileUtils.getOpenFileName(this, extension, description);
	}
}
