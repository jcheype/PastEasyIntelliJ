package com.jcheype.intellij.pasteasy;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.LightColors;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.prefs.Preferences;

/**
 * Created by IntelliJ IDEA.
 * User: mush
 * Date: Dec 2, 2009
 * Time: 10:50:19 PM
 */
public class PastEasy extends EditorAction {
    private static final Logger logger = Logger.getInstance(PastEasy.class.getName());

    public static final String PASTEASY_PREF = "com.jcheype.pasteasy.url";
    public static final String PASTEASY_DEFAULT = "http://localhost:8080";

    public PastEasy() {
        super(new PasteLineHandler());
    }

    private static class PasteLineHandler extends EditorWriteActionHandler {
        public void executeWriteAction(Editor editor, DataContext dataContext) {
            if (editor == null) {
                return;
            }
            String url = Preferences.userRoot().get(PASTEASY_PREF, PASTEASY_DEFAULT);

            SelectionModel selectionModel = editor.getSelectionModel();

            String selectedText = selectionModel.getSelectedText();

            if (selectedText == null || selectedText.trim().length() == 0) {
                selectedText = editor.getDocument().getText();
            }

            String language = "java";

            try {
                Project project = DataKeys.PROJECT.getData(dataContext);

                if (project != null) {

                    String result = pasteCode(language, selectedText, url);
                    setClipboardContents(result);


                    StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
                    if (statusBar != null) {// can be null if project has just been closed but task is already scheduled

                        JTextArea area = new JTextArea();
                        area.setOpaque(false);
                        area.setEditable(false);
                        area.setText(result + "\n\n Copied to clipboard");
                        area.setPreferredSize(new Dimension(300, 30));
                        Font font = new Font("Verdana", Font.PLAIN, 8);
                        area.setFont(font);

                        statusBar.fireNotificationPopup(area, LightColors.BLUE);

                        if (logger.isDebugEnabled()) {
                            logger.debug("Firing notification popup >" + area.getText() + "<");
                        }
                    }
                }
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }

        private void setClipboardContents(String aString) {
            StringSelection stringSelection = new StringSelection(aString);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, new ClipboardOwner() {
                public void lostOwnership(Clipboard clipboard, Transferable transferable) {
                }
            });
        }

        private String pasteCode(String language, String data, String urlString) {
            StringBuilder sb = new StringBuilder();
            try {
                // Construct data
                String buffer = URLEncoder.encode("language", "UTF-8") + "=" + URLEncoder.encode(language, "UTF-8");
                buffer += "&" + URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(data, "UTF-8");

                // Send data
                URL url = new URL(urlString + "/add.do");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(buffer);
                wr.flush();

                // Get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                wr.close();
                rd.close();
            } catch (Exception e) {
            }
            return urlString + "/show.do?id=" + sb.toString();
        }

    }


}
