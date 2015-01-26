package org.kitdroid.plugin;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import org.kitdroid.plugin.common.Log;
import org.kitdroid.plugin.common.Utils;
import org.kitdroid.plugin.gui.ModuleList;

import javax.swing.*;
import java.util.List;

/**
 * Created by huiyh on 15/1/26.
 */
public class Test {

    private JFrame mDialog;


    private void testPath(Project project, PsiFile sourceFile) {
        Log.i("execJavah");

        Log.i(project.getBasePath());//项目路径 /Users/huiyh/AndroidProjects/jniDemo

        Log.i(sourceFile.getName());// 文件名 MainActivity.java
        VirtualFile virtualFile = PsiUtilBase.getVirtualFile(sourceFile.getNavigationElement());
        String path = virtualFile.getPath();
        Log.i(path); // 文件路径 /Users/huiyh/AndroidProjects/jniDemo/app/src/main/java/com/huiyh/jnidemo/MainActivity.java

        Log.i(project.getProjectFilePath());
        Log.i(project.getWorkspaceFile().getPath());

        String[] moduleNames = Utils.getModuleNamesByGradleSettings(project);
    }


    protected void showModuleSelectDialog(Project project, Editor editor, List<String> moduleNames) {

        mDialog = new JFrame();
        mDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        ModuleList panel = new ModuleList(project,moduleNames, null);
        mDialog.getContentPane().add(panel);
        mDialog.pack();
        mDialog.setLocationRelativeTo(null);
        mDialog.setVisible(true);
    }

}
