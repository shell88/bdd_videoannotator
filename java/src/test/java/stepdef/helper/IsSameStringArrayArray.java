package stepdef.helper;

import org.mockito.ArgumentMatcher;

import com.github.shell88.bddvideoannotator.stubjava.StringArray;
import com.github.shell88.bddvideoannotator.stubjava.StringArrayArray;

/**
 * Mockito ArgumentMatchers that is used to compare two Datatable-Objects.
 * @author Hell
 *
 */

//RefEq doesn´t work here, maybe because
//StringArrayArray contains an object by itself

public class IsSameStringArrayArray implements ArgumentMatcher<StringArrayArray> {

  private StringArrayArray tableExpected;

  public IsSameStringArrayArray(StringArrayArray expected) {
    this.tableExpected = expected;
  }

  @Override
  public boolean matches(Object tableActualObj) {

    StringArrayArray tableActual = (StringArrayArray) tableActualObj;
    if (tableExpected.getItem().size() != tableActual.getItem().size()) {
      return false;
    }

    for (int rowIndex = 0; rowIndex < tableExpected.getItem().size(); rowIndex++) {

      StringArray rowExpected = tableExpected.getItem().get(rowIndex);
      StringArray rowActual = tableActual.getItem().get(rowIndex);
      
      if (rowExpected.getItem().size() != rowActual.getItem().size()) {
        return false;
      }

      for (int cellIndex = 0; cellIndex < rowExpected.getItem().size(); cellIndex++) {
        if (!rowExpected.getItem().get(cellIndex)
            .equals(rowActual.getItem().get(cellIndex))) {
          return false;
        }
      }
    }
    return true;

  }

}
