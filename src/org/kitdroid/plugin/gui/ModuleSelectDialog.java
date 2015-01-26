package org.kitdroid.plugin.gui;

import com.intellij.ui.components.JBScrollPane;
import org.kitdroid.plugin.common.Log;
import org.kitdroid.plugin.gui.i.OnSelectedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * Created by huiyh on 15/1/22.
 */
public class ModuleSelectDialog extends JFrame implements MouseListener {
    private final OnSelectedListener mListener;
    private final List<String> mNameList;

    public ModuleSelectDialog(java.util.List<String> names,OnSelectedListener listener) throws HeadlessException {
        super();
        mNameList = names;
        mListener = listener;
//        ModuleList panel = new ModuleList(names, listener);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    //        mDialog.getRootPane().setDefaultButton(panel.getConfirmButton());
    //    getContentPane().add(panel);
        creatContent();
        pack();
        setLocationRelativeTo(null);

    }

    private void creatContent() {
        JPanel contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(640, 360));
        contentPanel.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        contentPanel.add(creatItems());

        getContentPane().add(contentPanel);

    }



    private JBScrollPane creatItems() {

        JPanel injectionsPanel = new JPanel();
        injectionsPanel.setLayout(new BoxLayout(injectionsPanel, BoxLayout.PAGE_AXIS));
        injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel jLabel = new JLabel("Select module for *.h file.");
        injectionsPanel.add(jLabel);

        for (String name : mNameList) {
            JButton button = new JButton();
            button.setText(name);
            button.addMouseListener(this);

            injectionsPanel.add(button);
            injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        injectionsPanel.add(Box.createVerticalGlue());
        injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JBScrollPane scrollPane = new JBScrollPane(injectionsPanel);
        return scrollPane;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        Log.i("Button click: " + sourceButton.getText());
        if(mListener != null){
//            mListener.onSelected(,sourceButton.getName());
        }
        setVisible(false);
    }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
}
