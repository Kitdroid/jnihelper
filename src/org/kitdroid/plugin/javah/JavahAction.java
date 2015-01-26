package org.kitdroid.plugin.javah;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.util.PsiUtilBase;
import org.kitdroid.plugin.common.Log;
import org.kitdroid.plugin.common.Utils;
import org.kitdroid.plugin.gui.ModuleList;
import org.kitdroid.plugin.gui.i.OnSelectedListener;
import sun.jvm.hotspot.memory.PlaceholderTable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by huiyh on 15/1/21.
 */
public class JavahAction extends BaseGenerateAction implements OnSelectedListener {

    private JFrame mDialog;

    public JavahAction() {
        super(null);
    }
    public JavahAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    protected boolean isValidForClass(final PsiClass targetClass) {
        return ( super.isValidForClass(targetClass) && !(targetClass instanceof PsiAnonymousClass));
//        return true;
    }

    @Override
    public boolean isValidForFile(Project project, Editor editor, PsiFile file) {
        return (super.isValidForFile(project, editor, file));
//        return true;
    }


    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);

        actionPerformedImpl(project, editor);
    }

    @Override
    public void actionPerformedImpl(Project project, Editor editor) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiClass sourceClass = getTargetClass(editor, file);
        String sourceClassName = sourceClass.getName(); // 不含包名
        String qualifiedName = sourceClass.getQualifiedName(); // 包含包名
        boolean hasNativeMethod = Utils.hasNativeMethod(sourceClass);
        if(!hasNativeMethod){
            Utils.showErrorNotification(project,"This file has no native method.");
            return;
        }
//        execJavah(project,file);

        Utils.showInfoNotification(project,"Generate *.h for " + file.getName());
        ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        ProjectFileIndex projectFileIndex = rootManager.getFileIndex();
        VirtualFile[] contentRoots = rootManager.getContentRoots();
        VirtualFile[] contentRootsFromAllModules = rootManager.getContentRootsFromAllModules();
        final ProjectFileIndex fileIndex = rootManager.getFileIndex();
        VirtualFile sourceRootForFile = fileIndex.getSourceRootForFile(file.getVirtualFile()); // 默认包的路径 /XXX/src/main/java
        ArrayList<String> moduleNames = new ArrayList<String>();
        for(VirtualFile file1 : contentRoots){
            Module module = fileIndex.getModuleForFile(file1);

            moduleNames.add(module.getName());
        }
        if(moduleNames == null || moduleNames.isEmpty()){
            // TODO 在本Module中创建
        }else {
            // 选择Module
            showDialog(project,editor,moduleNames);
        }

        Log.i("sourceClass: "+sourceClass.getName());
    }

        // TODO exec javah
    private void execJavah(Project project,PsiClass sourceClass,PsiFile sourceFile,String moduleName) {
        String targetPath = moduleName;
        String sourcePath =  PsiUtilBase.getVirtualFile(sourceFile.getNavigationElement()).getPath();


        sourceClass.getName();
//        String sourceClassName = sourceFile.

        StringBuilder builder = new StringBuilder("javah -d ");
        builder.append(moduleName);
        builder.append(" -classpath ");
        builder.append(sourceFile);

        String[] envp = new String[5];

        envp[0] = "-d";
        envp[1] = targetPath;
        envp[2] = "-classpath";
        envp[3] = sourcePath;

        Utils.execute(builder.toString(),null);

    }

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


    protected void showDialog(Project project, Editor editor, List<String> moduleNames) {

        mDialog = new JFrame();
        mDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        ModuleList panel = new ModuleList(project,moduleNames, this);
        mDialog.getContentPane().add(panel);
        mDialog.pack();
        mDialog.setLocationRelativeTo(null);
        mDialog.setVisible(true);
    }

    @Override
    public void onSelected(Project project,JComponent view, String tag) {
        mDialog.setVisible(false);
//        execJavah(project,);
    }
}
