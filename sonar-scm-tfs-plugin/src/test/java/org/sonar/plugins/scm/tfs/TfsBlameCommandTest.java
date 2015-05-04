/*
 * SonarQube :: SCM :: TFS :: Plugin
 * Copyright (C) 2014 SonarSource
 * dev@sonar.codehaus.org
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
package org.sonar.plugins.scm.tfs;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.scm.BlameCommand.BlameInput;
import org.sonar.api.batch.scm.BlameCommand.BlameOutput;
import org.sonar.api.batch.scm.BlameLine;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TfsBlameCommandTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void ok() throws IOException {
    File executable = new File("src/test/resources/fake.bat");
    TfsBlameCommand command = new TfsBlameCommand(executable);

    File file = new File("src/test/resources/ok.txt");
    DefaultInputFile inputFile = new DefaultInputFile("ok", "ok.txt").setAbsolutePath(file.getAbsolutePath());

    BlameInput input = mock(BlameInput.class);
    when(input.filesToBlame()).thenReturn(Arrays.<InputFile>asList(inputFile));

    BlameOutput output = mock(BlameOutput.class);

    command.blame(input, output);

    verify(output).blameResult(
      inputFile,
      Arrays.asList(
        new BlameLine().date(new Date(1430736199000L)).revision("26274").author("SND\\DinSoft_cp"),
        new BlameLine().date(new Date(1430736200000L)).revision("26275").author("SND\\DinSoft_cp")));
  }

  @Test
  public void should_annotate_last_empty_line() {
    File executable = new File("src/test/resources/fake.bat");
    TfsBlameCommand command = new TfsBlameCommand(executable);

    File file = new File("src/test/resources/ok.txt");
    DefaultInputFile inputFile = new DefaultInputFile("ok", "ok.txt").setAbsolutePath(file.getAbsolutePath());
    inputFile.setLines(3);

    BlameInput input = mock(BlameInput.class);
    when(input.filesToBlame()).thenReturn(Arrays.<InputFile>asList(inputFile));

    BlameOutput output = mock(BlameOutput.class);

    command.blame(input, output);

    verify(output).blameResult(
      inputFile,
      Arrays.asList(
        new BlameLine().date(new Date(1430736199000L)).revision("26274").author("SND\\DinSoft_cp"),
        new BlameLine().date(new Date(1430736200000L)).revision("26275").author("SND\\DinSoft_cp"),
        new BlameLine().date(new Date(1430736200000L)).revision("26275").author("SND\\DinSoft_cp")));
  }

  @Test
  public void should_fail_with_local_change() {
    File executable = new File("src/test/resources/fake.bat");
    TfsBlameCommand command = new TfsBlameCommand(executable);

    File file = new File("src/test/resources/ko_local_change.txt");
    DefaultInputFile inputFile = new DefaultInputFile("ko_local_change", "ko_local_change.txt").setAbsolutePath(file.getAbsolutePath());

    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Unable to blame file ko_local_change.txt. No blame info at line 2. Is file commited?");

    BlameInput input = mock(BlameInput.class);
    when(input.filesToBlame()).thenReturn(Arrays.<InputFile>asList(inputFile));

    command.blame(input, mock(BlameOutput.class));
  }

  @Test
  public void file_not_found_error() {
    File executable = new File("src/test/resources/fake.bat");
    TfsBlameCommand command = new TfsBlameCommand(executable);

    File file = new File("src/test/resources/ko_non_existing.txt");
    DefaultInputFile inputFile = new DefaultInputFile("ko_non_existing", "ko_non_existing.txt").setAbsolutePath(file.getAbsolutePath());

    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("The TFS blame command " + executable.getAbsolutePath() + " failed with exit code 1");

    BlameInput input = mock(BlameInput.class);
    when(input.filesToBlame()).thenReturn(Arrays.<InputFile>asList(inputFile));

    command.blame(input, mock(BlameOutput.class));
  }

}
