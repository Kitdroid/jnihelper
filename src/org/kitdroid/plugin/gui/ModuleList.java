package org.kitdroid.plugin.gui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import org.kitdroid.plugin.common.Log;
import org.kitdroid.plugin.gui.i.OnSelectedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * Created by huiyh on 15/1/22.
 */
public class ModuleList extends JPanel implements ActionListener, MouseListener {

    private final OnSelectedListener mListener;
    private final List<String> mNameList;
    private final Project mProject;

    public ModuleList(Project project,List<String> names,OnSelectedListener listener) {
        super();
        mNameList = names;
        mListener = listener;
        mProject = project;

        setPreferredSize(new Dimension(200, 200));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        addItems();
    }

    private void addItems() {

        JPanel injectionsPanel = new JPanel();
        injectionsPanel.setLayout(new BoxLayout(injectionsPanel, BoxLayout.PAGE_AXIS));

        JLabel jLabel = new JLabel("Select module for *.h file.");
        injectionsPanel.add(jLabel);
        injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        for (String name : mNameList) {
            JButton button = new JButton();
            button.setText(name);
            button.addActionListener(this);
            button.addMouseListener(this);

            injectionsPanel.add(button);
            injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        }

        JBScrollPane scrollPane = new JBScrollPane(injectionsPanel);
        add(scrollPane,BorderLayout.CENTER);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JButton sourceButton = (JButton) e.getSource();
        Log.i("Button click: "+sourceButton.getText());
        if(mListener != null){
            mListener.onSelected(mProject,sourceButton,sourceButton.getName());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
