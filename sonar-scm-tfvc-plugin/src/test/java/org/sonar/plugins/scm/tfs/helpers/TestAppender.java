/*
 * SonarQube :: SCM :: TFVC :: Plugin
 * Copyright (C) 2014 SonarSource
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.scm.tfs.helpers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

import java.util.ArrayList;
import java.util.List;

public class TestAppender extends ConsoleAppender{

  private List<String> errorEvents = new ArrayList<>();

 @Override
  public void doAppend(Object eventObject) {
    ILoggingEvent event = (ILoggingEvent) eventObject;

    if(event.getLevel() == Level.ERROR) {
        errorEvents.add(event.getMessage());
    }
  }

  public List<String> getErrorEvents(){
    return errorEvents;
  }

}
