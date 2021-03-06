/*******************************************************************************
 * Copyright (c) 2006, 2015 PalmSource, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Ewa Matejska (PalmSource)
 * 
 * Referenced GDBDebuggerPage code to write this.
 * Anna Dushistova (Mentor Graphics) - moved to org.eclipse.cdt.launch.remote.tabs
 * Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.gui;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.cdt.dsf.gdb.internal.ui.launching.GdbDebuggerPage;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.internal.launch.remote.Messages;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.swt.widgets.TabFolder;
import com.arc.embeddedcdt.common.LaunchFileFormatVersionChecker;
import com.arc.embeddedcdt.dsf.utils.ConfigurationReader;
import com.arc.embeddedcdt.dsf.utils.ConfigurationWriter;

/**
 * The dynamic debugger tab for remote launches using gdb server. The gdbserver settings are used to
 * start a gdbserver session on the remote and then to connect to it from the host. The DSDP-TM
 * project is used to accomplish this.
 */
@SuppressWarnings("restriction")
public class RemoteGdbDebuggerPage extends GdbDebuggerPage {

    private DebuggerGroupContainer debuggerGroupContainer = new DebuggerGroupContainer();

    public RemoteGdbDebuggerPage() {
      debuggerGroupContainer.addObserver(new Observer() {

        @Override
        public void update(Observable o, Object arg) {
          if (arg == null)
            updateLaunchConfigurationDialog();
          else
            setErrorMessage((String) arg);
        }
      });
    }

    @Override
    public String getName() {
        return Messages.Remote_GDB_Debugger_Options;
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
      super.setDefaults(configuration);
      debuggerGroupContainer.setDefaults(configuration);
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        LaunchFileFormatVersionChecker.getInstance().check(configuration);
        super.initializeFrom(configuration);
        debuggerGroupContainer.initializeFrom(configuration);
        fGDBCommandText.setText(debuggerGroupContainer.getGdbPath());
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        super.performApply(configuration);
        ConfigurationReader configurationReader = new ConfigurationReader(configuration);
        final String programName = configurationReader.getProgramName();
        configuration.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME,
            programName.replace('\\', '/'));

        ConfigurationWriter configurationWriter = new ConfigurationWriter(configuration);
        debuggerGroupContainer.setGdbPath(fGDBCommandText.getText());
        debuggerGroupContainer.performApply(configurationWriter, configuration);
    }

    @Override
    public boolean isValid(ILaunchConfiguration configuration) {
        setErrorMessage(null);
        setMessage(null);
        return debuggerGroupContainer.isValid(configuration);
    }

    @Override
    public void createTabs(TabFolder tabFolder) {
        super.createTabs(tabFolder);
        debuggerGroupContainer.createGdbServerSettingsTab(tabFolder);
    }
    
    @Override
    public void setLaunchConfigurationDialog(ILaunchConfigurationDialog dialog) {
        super.setLaunchConfigurationDialog(dialog);
        debuggerGroupContainer.getWorkingDirectoryBlockNsim().setLaunchConfigurationDialog(dialog);
    }

    @Override
    public String getErrorMessage() {
        return debuggerGroupContainer.getErrorMessage(super.getErrorMessage());
    }
}
