package org.kitdroid.plugin.javah;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilBase;
import org.kitdroid.plugin.common.Log;
import org.kitdroid.plugin.common.Utils;
import org.kitdroid.plugin.gui.ModuleList;
import org.kitdroid.plugin.gui.i.OnSelectedListener;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

/**
 * Created by huiyh on 15/1/21.
 */
public class JavahAction extends BaseGenerateAction {

    public JavahAction() {
        super(null);
    }
    public JavahAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    protected boolean isValidForClass(final PsiClass targetClass) {
        //只对java类有效
        return ( super.isValidForClass(targetClass) && !(targetClass instanceof PsiAnonymousClass));
//        return true;
    }

    @Override
    public boolean isValidForFile(Project project, Editor editor, PsiFile file) {
        //只对java类有效
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

        boolean hasNativeMethod = Utils.hasNativeMethod(sourceClass);
        if(!hasNativeMethod){
            Utils.showErrorNotification(project,"This file has no native method.");
            return;
        }

        ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        final ProjectFileIndex fileIndex = rootManager.getFileIndex();
        VirtualFile sourceRootFile = fileIndex.getSourceRootForFile(file.getVirtualFile()); // 默认包的路径 /XXX/src/main/java

        createJavahFile(project, sourceRootFile, sourceClass);
    }

    private void createJavahFile(Project project, VirtualFile sourceRootFile, PsiClass sourceClass) {
        Utils.showInfoNotification(project,"Generate *.h for " + sourceClass.getName());

        VirtualFile sourceParentFile = sourceRootFile.getParent();
        VirtualFile sourceJniFile = findOrCreateJinDir(project, sourceParentFile);

        if(sourceJniFile == null){
            return;
        }
        String targetPath = sourceJniFile.getPath();
        String sourcePath = sourceRootFile.getPath();
        String sourceClassName = sourceClass.getQualifiedName(); // 包含包名

        execJavah(targetPath, sourcePath, sourceClassName);
    }

    private VirtualFile findOrCreateJinDir(Project project, VirtualFile sourceParentFile) {
        VirtualFile jniDir = sourceParentFile.findChild("jni");
        if(jniDir == null || !jniDir.exists()){
            try {
                jniDir = sourceParentFile.createChildDirectory(project, "jni");
            } catch (IOException e) {
                jniDir = null;
//                Utils.showErrorNotification(project, "Creat \"jin\" directory fail !");
                Messages.showErrorDialog("Create \"jin\" directory fail !\nYou can create it manually.","Error");
                e.printStackTrace();
            }
        }else if(!jniDir.isDirectory()){
            Messages.showErrorDialog("Create \"jin\" directory fail !\n Required a directory,but is a file.","Error");
            jniDir = null;
        }
        return jniDir;
    }


    private void execJavah(String targetPath, String sourcePath, String sourceClassName) {
        StringBuilder builder = new StringBuilder("javah -d ");
        builder.append(targetPath);
        builder.append(" -classpath ");
        builder.append(sourcePath);
        builder.append(" ");
        builder.append(sourceClassName);

        Utils.execute(builder.toString(), null);
    }
    private void execJavah2(String targetPath, String sourcePath, String sourceClassName) {
        String[] envp = new String[5];

        envp[0] = "-d";
        envp[1] = targetPath;
        envp[2] = "-classpath";
        envp[3] = sourcePath;
        envp[4] = sourceClassName;

        Utils.execute("javah",envp);
    }

}
