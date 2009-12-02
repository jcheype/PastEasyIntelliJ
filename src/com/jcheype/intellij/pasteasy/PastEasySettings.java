package com.jcheype.intellij.pasteasy;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.prefs.Preferences;

/**
 * Created by IntelliJ IDEA.
 * User: mush
 * Date: Dec 2, 2009
 * Time: 11:55:52 PM
 */
public class PastEasySettings implements ApplicationComponent, Configurable {
    private JTextField urlField;
    private JPanel panel1;

    @NotNull
    public String getComponentName() {
        return "pasteasysettings";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    @Nls
    public String getDisplayName() {
        return "PastEasy";
    }

    public Icon getIcon() {
        return null;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        return panel1;
    }

    public boolean isModified() {
        return urlField.getText() != null && urlField.getText() != Preferences.userRoot().get(PastEasy.PASTEASY_PREF, PastEasy.PASTEASY_DEFAULT);
    }

    public void apply() throws ConfigurationException {
        if (urlField.getText() != null && urlField.getText().trim().length() > 0) {
            Preferences.userRoot().put(PastEasy.PASTEASY_PREF, urlField.getText());
        }
    }

    public void reset() {
        urlField.setText(Preferences.userRoot().get(PastEasy.PASTEASY_PREF, PastEasy.PASTEASY_DEFAULT));
    }

    public void disposeUIResources() {
    }
}
