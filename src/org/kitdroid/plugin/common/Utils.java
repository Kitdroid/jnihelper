package org.kitdroid.plugin.common;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.MethodSignature;
import com.intellij.psi.util.PsiEditorUtil;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by huiyh on 15/1/21.
 */
public class Utils {


    public static boolean hasNativeMethod(PsiClass psiClass) {
        PsiMethod[] allMethods = psiClass.getMethods();
        for(PsiMethod method : allMethods){
            PsiElement psiElement = method.getNavigationElement().getFirstChild();
            boolean hasNative = psiElement.getText().contains("native");
            if (hasNative){
                return true;
            }
        }
        return false;
    }

    /**
     * Display simple notification - information
     *
     * @param project
     * @param text
     */
    public static void showInfoNotification(Project project, String text) {
        showNotification(project, MessageType.INFO, text);
    }


    /**
     * Display simple notification - error
     *
     * @param project
     * @param text
     */
    public static void showErrorNotification(Project project, String text) {
        showNotification(project, MessageType.ERROR, text);
    }


    /**
     * Display simple notification of given type
     *
     * @param project
     * @param type
     * @param text
     */
    public static void showNotification(Project project, MessageType type, String text) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(text, type, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }


    public static String execute(String command, String[] envp) {
        try {
            System.out.println("\nExecute command:" + command + " " + Arrays.toString(envp));
            Process process = Runtime.getRuntime().exec(command, envp);//调用本地程序
//            ProcessBuilder pb = new ProcessBuilder(command);
//            Process process = pb.start();
            System.out.println("getShellOut:" + getShellOut(process));
            process.waitFor();
            System.out.println("return code: " + process.exitValue());

            return "";
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 读取输出流数据
     *
     * @param p 进程
     * @return 从输出流中读取的数据
     * @throws IOException
     */
    public static final String getShellOut(Process p) throws IOException{

        StringBuilder sb = new StringBuilder();
        BufferedInputStream in = null;
        BufferedReader br = null;

        try {

            in = new BufferedInputStream(p.getErrorStream());
            br = new BufferedReader(new InputStreamReader(in));
            String s;

            while ((s = br.readLine()) != null) {
                sb.append("\n");
                sb.append(s);
            }

        } catch (IOException e) {
            throw e;
        } finally {
            br.close();
            in.close();
        }

        return sb.toString();
    }


    public static String[] getModuleNamesByGradleSettings(Project project) {
        PsiFile settingsFile = getGradleSettingsFile(project);
        if(settingsFile == null){
            Log.i("no setting file");
            return new String[0];
        }
        PsiElement navigationElement = settingsFile.getNavigationElement();
        PsiElement[] children = navigationElement.getChildren();
        for(PsiElement element : children){
            System.out.println("Setting element: "+element.getText());
            if(element.getText().contains("include")){
                String[] moduleNames = element.getText().replace("include", "").replaceAll("'", "").replaceAll(":", "").replaceAll(" ", "").split(",");
                Log.i("moduleNames: "+ Arrays.toString(moduleNames));
                return moduleNames;
            }
        }
        return new String[0];
    }

    private static PsiFile getGradleSettingsFile(Project project) {
        PsiFile[] files = FilenameIndex.getFilesByName(project, "settings.gradle", new EverythingGlobalScope(project));
        if (files.length <= 0) {
            return null; //no matching files
        }
        return files[0];// 根目录下的
    }


}
