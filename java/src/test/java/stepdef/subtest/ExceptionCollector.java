package stepdef.subtest;

import java.util.ArrayList;
import java.util.List;

/**
 * Used in {@link CucumberSubTestThreadWithAdapterInstance} to collect all
 * Exceptions to a list for later use.
 * 
 * @author Hell
 *
 */

public class ExceptionCollector implements Thread.UncaughtExceptionHandler {

  private List<Throwable> exceptions;

  public ExceptionCollector() {
    exceptions = new ArrayList<Throwable>();
  }

  public void uncaughtException(Thread thread, Throwable error) {
    exceptions.add(error);

  }

  public List<Throwable> getThrownExceptions() {
    return this.exceptions;
  }

}
