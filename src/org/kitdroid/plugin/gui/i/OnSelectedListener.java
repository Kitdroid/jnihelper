package org.kitdroid.plugin.gui.i;

import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * Created by huiyh on 15/1/22.
 */
public interface OnSelectedListener {
    public void onSelected(Project project,JComponent view,String tag);
}
