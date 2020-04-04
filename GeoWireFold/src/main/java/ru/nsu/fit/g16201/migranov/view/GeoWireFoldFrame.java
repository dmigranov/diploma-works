package ru.nsu.fit.g16201.migranov.view;

import ru.nsu.fit.g16201.migranov.controller.Controller;
import ru.nsu.fit.g16201.migranov.model.Geodesic;
import ru.nsu.fit.g16201.migranov.view.frametemplate.MainFrame;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class GeoWireFoldFrame extends MainFrame {
    private JLabel statusLabel = new JLabel("");

    private List<AbstractButton> deactivatedButtons = new ArrayList<>();
    private boolean fileIsLoaded = false;

    private Controller controller;
    private WireframePanel wireframePanel;


    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JPanel geodesicsPanel;


    private JTextField nField, mField, kField, swField, shField, znField, TiField, TjField;
    private JColorChooser backgroundColorChooser, figureColorChooser, geodesicColorChooser;
    private JButton confirmButton;


    private JTextField uStartField, vStartField, uDirField, vDirField;
    private JList geoList;
    private DefaultListModel<String> geoListModel;
    private JButton addGeodesicButton, removeGeodesicButton, saveGeodesicButton;


    public static void main(String[] args) throws Exception {
        new GeoWireFoldFrame();
    }

    private void resize() {
        if(!fileIsLoaded)
            return;
        int width = mainPanel.getWidth();
        int height = mainPanel.getHeight();
        double sw = controller.getSw(), sh = controller.getSh();

        double nwidth, nheight;
        if(width < height) {
            nheight = height;
            nwidth = sw / sh * height;
            if(nwidth > width)
            {
                nheight = nheight / nwidth * width;
                nwidth = width;
            }
        }
        else
        {
            nwidth = width;
            nheight = sh / sw * width;
            if(nheight > height)
            {
                nwidth = nwidth/ nheight * height;
                nheight = height;
            }
        }
        wireframePanel.setPreferredSize(new Dimension((int)Math.round(nwidth) - 20, (int)Math.round(nheight) - 20));
        controller.drawFigure();
        mainPanel.revalidate();
    }

    private GeoWireFoldFrame() throws Exception {
        super(800, 600, "Untitled | Denis Migranov, 16201");

        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resize();
            }
        });
        wireframePanel = new WireframePanel();
        mainPanel.add(wireframePanel);
        controller = new Controller(wireframePanel);
        addMenus();
        createCommonConfigurationPanel();
        createGeodesicsConfigurationPanel();

        add(mainPanel);

        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createGeodesicsConfigurationPanel() {
        geodesicsPanel = new JPanel();

        geoListModel = new DefaultListModel<String>();
        geoList = new JList(geoListModel);

        geoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        geoList.setLayoutOrientation(JList.VERTICAL);

        JScrollPane geoScrollPane = new JScrollPane(geoList);
        geodesicsPanel.add(geoScrollPane);
        JPanel geodesicPropertiesPanel = new JPanel();
        geodesicPropertiesPanel.setLayout(new BoxLayout(geodesicPropertiesPanel, BoxLayout.Y_AXIS));
        JPanel uvPanel = new JPanel(new GridLayout(3, 2, 0, 0));
        uStartField = new JTextField();
        vStartField = new JTextField();
        uDirField = new JTextField();
        vDirField = new JTextField();

        geodesicColorChooser = new JColorChooser();
        {
            AbstractColorChooserPanel[] panels = geodesicColorChooser.getChooserPanels();
            for (AbstractColorChooserPanel p : panels)
                if (!p.getDisplayName().equals("RGB"))
                    geodesicColorChooser.removeChooserPanel(p);
            geodesicColorChooser.setPreviewPanel(new JPanel());
            geodesicColorChooser.setColor(controller.getBackgroundColor());
        }

        uvPanel.add(new LabelTextField("u0: ", uStartField, new FloatTextFieldKeyListener()));
        uvPanel.add(new LabelTextField("v0: ", vStartField, new FloatTextFieldKeyListener()));
        uvPanel.add(new LabelTextField("u̇0: ", uDirField, new FloatTextFieldKeyListener()));
        uvPanel.add(new LabelTextField("v̇0: ", vDirField, new FloatTextFieldKeyListener()));

        addGeodesicButton = new JButton("Add Geodesic");
        removeGeodesicButton = new JButton("Remove Geodesic");
        removeGeodesicButton.setEnabled(false);
        saveGeodesicButton = new JButton("Apply");
        saveGeodesicButton.setEnabled(false);

        addGeodesicButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        removeGeodesicButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveGeodesicButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        geodesicPropertiesPanel.add(uvPanel);
        geodesicPropertiesPanel.add(addGeodesicButton);
        geodesicPropertiesPanel.add(Box.createVerticalStrut(5));
        geodesicPropertiesPanel.add(removeGeodesicButton);
        geodesicPropertiesPanel.add(Box.createVerticalStrut(5));
        geodesicPropertiesPanel.add(saveGeodesicButton);

        geodesicsPanel.add(geodesicPropertiesPanel);
        geodesicsPanel.add(geodesicColorChooser);

        geoList.addListSelectionListener(e -> {
            int index = geoList.getSelectedIndex();
            updateGeodesicFields(index);
        });

        addGeodesicButton.addActionListener(e -> {
            controller.addGeodesic();
            updateFields();
        });

        removeGeodesicButton.addActionListener(e -> {
            int index = geoList.getSelectedIndex();
            if(index == -1)
                return;
            controller.removeGeodesic(index);
            updateFields();
        });

        saveGeodesicButton.addActionListener(e -> {
            int index = geoList.getSelectedIndex();
            if(index == -1)
                return;

            try
            {
                double uStart, uDir, vStart, vDir;

                uStart = Double.parseDouble(uStartField.getText());
                vStart = Double.parseDouble(vStartField.getText());
                uDir = Double.parseDouble(uDirField.getText());
                vDir = Double.parseDouble(vDirField.getText());

                controller.changeGeodesic(index, uStart, vStart, uDir, vDir, geodesicColorChooser.getColor());

                updateGeodesicFields(index);
            }
            catch (NumberFormatException n)
            {
                JOptionPane.showMessageDialog(GeoWireFoldFrame.this, n.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void updateGeodesicFields(int index) {
        if (index == -1) {
            removeGeodesicButton.setEnabled(false);
            saveGeodesicButton.setEnabled(false);
            uStartField.setText("");
            uDirField.setText("");
            vStartField.setText("");
            vDirField.setText("");
            geodesicColorChooser.setColor(Color.BLACK);

        }
        else {
            removeGeodesicButton.setEnabled(true);
            saveGeodesicButton.setEnabled(true);
            var geodesic = controller.getGeodesic(index);
            uStartField.setText(String.format("%.3f", geodesic.getuStart()));
            uDirField.setText(String.format("%.3f", geodesic.getuDir()));
            vStartField.setText(String.format("%.3f", geodesic.getvStart()));
            vDirField.setText(String.format("%.3f", geodesic.getvDir()));
            geodesicColorChooser.setColor(geodesic.getColor());
        }
    }


    private void createCommonConfigurationPanel() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            private final Insets borderInsets = new Insets(0, 0, 0, 0);
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }
            @Override
            protected Insets getContentBorderInsets(int tabPlacement) {
                return borderInsets;
            }
        });     //без этого были странные белые полоски под вкладками https://stackoverflow.com/questions/5183687/java-remove-margin-padding-on-a-jtabbedpane

        JPanel commonPanel = new JPanel();
        tabbedPane.add("Common", commonPanel);

        JPanel spline3DConfigurationPanel = new JPanel();
        tabbedPane.add("3D Spline Config Panel", spline3DConfigurationPanel);

        commonPanel.setLayout(new BoxLayout(commonPanel, BoxLayout.Y_AXIS));
        spline3DConfigurationPanel.setLayout(new BoxLayout(spline3DConfigurationPanel, BoxLayout.Y_AXIS));
        JPanel mnkPanel = new JPanel(new GridLayout(1, 3)), clippingPanel = new JPanel(new GridLayout(1, 4)), TijPanel = new JPanel(new GridLayout(1, 2));

        nField = new JTextField();
        mField = new JTextField();
        kField = new JTextField();
        mnkPanel.add(new LabelTextField("n: ", nField, new IntegerTextFieldKeyListener()));
        mnkPanel.add(new LabelTextField("m: ", mField, new IntegerTextFieldKeyListener()));
        mnkPanel.add(new LabelTextField("k: ", kField, new IntegerTextFieldKeyListener()));

        swField = new JTextField();
        shField = new JTextField();
        znField = new JTextField();
        clippingPanel.add(new LabelTextField("sw: ", swField, new FloatTextFieldKeyListener()));
        clippingPanel.add(new LabelTextField("sh: ", shField, new FloatTextFieldKeyListener()));
        clippingPanel.add(new LabelTextField("Znear: ", znField, new FloatTextFieldKeyListener()));

        TiField = new JTextField();
        TjField = new JTextField();
        TijPanel.add(new LabelTextField("Ti: ", TiField, new IntegerTextFieldKeyListener()));
        TijPanel.add(new LabelTextField("Tj: ", TjField, new IntegerTextFieldKeyListener()));

        backgroundColorChooser = new JColorChooser();
        figureColorChooser = new JColorChooser();
        {
            AbstractColorChooserPanel[] panels = backgroundColorChooser.getChooserPanels();
            for (AbstractColorChooserPanel p : panels)
                if (!p.getDisplayName().equals("RGB"))
                    backgroundColorChooser.removeChooserPanel(p);
            backgroundColorChooser.setPreviewPanel(new JPanel());
            backgroundColorChooser.setColor(controller.getBackgroundColor());
        }
        {
            AbstractColorChooserPanel[] panels = figureColorChooser.getChooserPanels();
            for (AbstractColorChooserPanel p : panels)
                if (!p.getDisplayName().equals("RGB"))
                    figureColorChooser.removeChooserPanel(p);
            figureColorChooser.setPreviewPanel(new JPanel());
            figureColorChooser.setColor(controller.getBackgroundColor());
        }
        commonPanel.add(Box.createVerticalStrut(20));
        commonPanel.add(clippingPanel);
        commonPanel.add(backgroundColorChooser);

        spline3DConfigurationPanel.add(mnkPanel);
        spline3DConfigurationPanel.add(TijPanel);
        //spline3DConfigurationPanel.add(new JLabel("Ti & Tj are orders of spline basis functions; the smoothness of surface depends on Ni, Ti, Nj, Tj (dependency is given by knot points)"));
        spline3DConfigurationPanel.add(figureColorChooser);

        confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> {
            try
            {
                double zn, sw, sh;

                sw = Double.parseDouble(swField.getText());
                sh = Double.parseDouble(shField.getText());
                zn = Double.parseDouble(znField.getText());

                if(!(zn > 0 && sw > 0 && sh > 0))
                    throw new NumberFormatException("Wrong clipping");

                int n, m, k, Ti, Tj;
                n = Integer.parseInt(nField.getText());
                k = Integer.parseInt(kField.getText());
                m = Integer.parseInt(mField.getText());
                Ti = Integer.parseInt(TiField.getText());
                Tj = Integer.parseInt(TjField.getText());
                if(1 > Ti || 1 > Tj || Ti > controller.getNi() || Tj > controller.getNj())
                    throw new NumberFormatException("Wrong Ti or Tj, 1 <= Ti <= Ni");
                if(m <= 0 || n <= 0 || k <= 0)
                    throw new NumberFormatException("Wrong m, n, or k");

                controller.setConstants(n, m, k, sw, sh, zn, zn + 100, backgroundColorChooser.getColor(), figureColorChooser.getColor(), Ti, Tj);

                resize();

                updateFields();
            }
            catch (NumberFormatException n)
            {
                JOptionPane.showMessageDialog(GeoWireFoldFrame.this, n.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void addMenus() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuAndToolBarButton("File/Open 3D Spline file", "Open a 3D Spline file", KeyEvent.VK_T, "upload-1.png", "onOpen3D", false);
        //addMenuAndToolBarButton("File/Save as", "Save figures as", KeyEvent.VK_S, "download.png", "onSave", true);

        addSubMenu("Options", KeyEvent.VK_O);
        addMenuAndToolBarButton("Options/Configuration", "Configure splines and viewing properties", KeyEvent.VK_S, "settings.png", "onConfigure", true);
        addMenuAndToolBarButton("Options/Geodesics", "Configure geodesics", KeyEvent.VK_G, "settings.png", "onConfigureGeodesics", true);


        addSubMenu("Help", KeyEvent.VK_H);
        addMenuAndToolBarButton("Help/About", "Shows program version and copyright information", KeyEvent.VK_A, "book.png", "onAbout", false);
    }

    private void addMenuAndToolBarButton(String path, String tooltip, int mnemonic, String icon, String actionMethod, boolean isDeactivated) throws NoSuchMethodException
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
                method.invoke(GeoWireFoldFrame.this);
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

    private void updateFields() {

        nField.setText(controller.getN() + "");
        mField.setText(controller.getM() + "");
        kField.setText(controller.getK() + "");
        znField.setText(controller.getZn() + "");
        shField.setText(controller.getSh() + "");
        swField.setText(controller.getSw() + "");
        TiField.setText(controller.getTi() + "");
        TjField.setText(controller.getTj() + "");
        backgroundColorChooser.setColor(controller.getBackgroundColor());
        figureColorChooser.setColor(controller.getFigureColor());

        geoListModel.clear();
        int geoCount = controller.getGeodesicsCount();
        for(int i = 1; i < geoCount + 1; i++)
            geoListModel.addElement("Geodesic " + i);
    }

    public void onOpen3D() throws NoSuchMethodException
    {
        File file = getOpenFileName("txt", "A 3D Spline description file");
        load3DFile(file);
    }



    private void load3DFile(File file)
    {
        if(file != null) {
            setTitle(file.getName() + " | Denis Migranov, 16201");
            int r = controller.load3DFile(file);
            if(r == 0)
            {
                for (AbstractButton b : deactivatedButtons)
                {
                    b.setEnabled(true);
                }
                fileIsLoaded = true;
                wireframePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                resize();
            }
            else
            {
                wireframePanel.setBorder(BorderFactory.createEmptyBorder());

                fileIsLoaded = false;
                JOptionPane.showMessageDialog(this, "Wrong file format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onSave()
    {
        File file = getSaveFileName("png", "A PNG file");
        if (file != null) {
            controller.saveFile(file);
        }
    }

    public void onAbout()
    {
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
        aboutPanel.add(new JLabel("Denis Migranov, Novosibirsk State University, IT faculty, group 16201, 2019-2020"));
        aboutPanel.add(new JLabel("Icons used are from www.flaticon.com/packs/multimedia-collection and icons8.com"));
        JOptionPane.showMessageDialog(this, aboutPanel, "GeoWireFold", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onConfigureGeodesics()
    {
        //выводить окно со списком геодезических.
        //как задавать: начальная точка +...
        //вектор? и потом по нему идти
        updateFields();
        if(JOptionPane.showConfirmDialog(this, geodesicsPanel, "Options", JOptionPane.DEFAULT_OPTION) == JOptionPane.OK_OPTION)
        {

        }

    }

    public void onConfigure()
    {
        updateFields();
        JOptionPane.showOptionDialog(this, tabbedPane, "Configuration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{confirmButton}, confirmButton);
    }
}