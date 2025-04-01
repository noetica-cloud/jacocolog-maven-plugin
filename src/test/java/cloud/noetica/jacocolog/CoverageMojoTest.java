package cloud.noetica.jacocolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.Rule;
import org.junit.Test;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;

public class CoverageMojoTest {

  @Rule
  public MojoRule rule = new MojoRule();

  @Rule
  public TestResources resources = new TestResources();

  @Test
  public void test_coverage_logging_on_simple_project() throws Exception {
    // 1. Build the test project to generate jacoco.exec
    File testProjectDir = resources.getBasedir("simple");
    ProcessBuilder builder = new ProcessBuilder("mvn", "clean", "test");
    builder.directory(testProjectDir);
    builder.redirectErrorStream(true);
    Process process = builder.start();
    // Consume and log the output if needed
    process.waitFor();
    assertEquals("Maven test phase did not complete successfully", 0, process.exitValue());

    // Capture standard output
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(baos));

    // 2. Execute your JacocoLogMojo
    CoverageMojo mojo = (CoverageMojo) rule.lookupConfiguredMojo(testProjectDir, "coverage");
    try {
      mojo.execute();
    } finally {
      // Restore original System.out
      System.setOut(originalOut);
    }

    // Add assertions here
    String output = baos.toString("UTF-8");
    assertTrue("Expected \"Test Coverage\" log output not found", output.contains("Test Coverage:"));
    assertTrue("Expected \"Class Coverage\" log output not found", output.contains("- Class Coverage: 55.55%"));
    assertTrue("Expected \"Method Coverage\" log output not found", output.contains("- Method Coverage: 66.66%"));
    assertTrue("Expected \"Branch Coverage\" log output not found", output.contains("- Branch Coverage: unknown"));
    assertTrue("Expected \"Line Coverage\" log output not found", output.contains("- Line Coverage: 50%"));
    assertTrue("Expected \"Instruction Coverage\" log output not found",
        output.contains("- Instruction Coverage: 55.55%"));
    assertTrue("Expected \"Complexity Coverage\" log output not found",
        output.contains("- Complexity Coverage: 66.66%"));
  }

  @Test
  public void test_coverage_logging_on_special_configured_project() throws Exception {
    // 1. Build the test project to generate jacoco.exec
    File testProjectDir = resources.getBasedir("configured-differently");
    ProcessBuilder builder = new ProcessBuilder("mvn", "clean", "test");
    builder.directory(testProjectDir);
    builder.redirectErrorStream(true);
    Process process = builder.start();
    // Consume and log the output if needed
    process.waitFor();
    assertEquals("Maven test phase did not complete successfully", 0, process.exitValue());

    // Capture standard output
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(baos));

    // 2. Execute your JacocoLogMojo
    CoverageMojo mojo = (CoverageMojo) rule.lookupConfiguredMojo(testProjectDir, "coverage");
    try {
      mojo.execute();
    } finally {
      // Restore original System.out
      System.setOut(originalOut);
    }

    // Add assertions here
    String output = baos.toString("UTF-8");
    assertTrue("Expected \"Test Coverage\" log output not found", output.contains("Test Coverage:"));
    assertTrue("Expected \"Class Coverage\" log output not found", output.contains("- Class Coverage: 55.5%"));
    assertFalse("Expected \"Method Coverage\" log output found", output.contains("- Method Coverage: 66.6%"));
    assertFalse("Expected \"Branch Coverage\" log output found", output.contains("- Branch Coverage: unknown"));
    assertFalse("Expected \"Line Coverage\" log output found", output.contains("- Line Coverage: 50%"));
    assertTrue("Expected \"Instruction Coverage\" log output not found",
        output.contains("- Instruction Coverage: 55.5%"));
    assertTrue("Expected \"Complexity Coverage\" log output not found",
        output.contains("- Complexity Coverage: 66.6%"));
  }
}