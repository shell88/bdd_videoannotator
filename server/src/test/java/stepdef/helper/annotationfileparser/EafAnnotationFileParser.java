package stepdef.helper.annotationfileparser;

import com.github.shell88.bddvideoannotator.service.StepResult;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

/**
 * AnnotationFileParser for the EAF Format EAF.
 * @author Hell
 *
 */

public class EafAnnotationFileParser extends AnnotationFileParser {
  /**
   *   XML-Doc-Element of the annotationFile that could be parsed.
   */
  private Document doc;

  /**
   * Initializes {@link #doc}.
   * @param annotationFile                 The eaf-annotation file to be parsed.
   * @throws ParserConfigurationException  ParserException
   * @throws SAXException                  ParserException
   * @throws IOException                   When eaf-annotation file cannot be opened.
   */
  public EafAnnotationFileParser(File annotationFile)
      throws ParserConfigurationException, SAXException, IOException {
    super(annotationFile);
    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = domFactory.newDocumentBuilder();
    doc = builder.parse(annotationFile);
  }

  @Override
  public String parseVideoReferenceFile() throws Throwable {
    XPath xpath = XPathFactory.newInstance().newXPath();
    XPathExpression expr = xpath
        .compile("//ANNOTATION_DOCUMENT/HEADER/MEDIA_DESCRIPTOR/@MEDIA_URL");
    return (String) expr.evaluate(doc, XPathConstants.STRING);
  }

  /**
   * @return All timeslot elements from the eaf file.
   * @throws Throwable ParserExceptions
   */
  private Map<String, Long> parseTimeSlotsOfEAafOutputFile() throws Throwable {
    Map<String, Long> timeslots = new HashMap<String, Long>();

    XPath xpath = XPathFactory.newInstance().newXPath();
    XPathExpression exprTimeslots = xpath
        .compile("//ANNOTATION_DOCUMENT/TIME_ORDER/TIME_SLOT");
    NodeList timeslotNodes = (NodeList) exprTimeslots.evaluate(doc,
        XPathConstants.NODESET);

    for (int i = 0; i < timeslotNodes.getLength(); i++) {
      timeslots.put(
          timeslotNodes.item(i).getAttributes().getNamedItem("TIME_SLOT_ID")
              .getNodeValue(),
          Long.parseLong(timeslotNodes.item(i).getAttributes()
              .getNamedItem("TIME_VALUE").getNodeValue()));
    }
    return timeslots;
  }

  @Override
  public List<ResultStep> parseSteps() throws Throwable {

    /*
     * A step annotation is formatted as follows in eaf: <steptext> (||line 1
     * value 1 | line 2 value 2 || line 2 value 1 | line 2 value 2||) <RESULT>
     * The datatable is optional.
     */

    List<ResultStep> stepList = new ArrayList<ResultStep>();
    Map<String, Long> timeslots = parseTimeSlotsOfEAafOutputFile();

    XPath xpath = XPathFactory.newInstance().newXPath();
    XPathExpression exprStep = xpath
        .compile("//ANNOTATION_DOCUMENT/TIER[@TIER_ID=\"Steps\"]//ANNOTATION_VALUE");
    NodeList steps = (NodeList) exprStep
        .evaluate(doc, XPathConstants.NODESET);

    String refSlotStart;
    String refSlotEnd;

    Pattern resultPattern = Pattern.compile(".* ("
        + StringUtils.join(StepResult.values(), "|") + ")$");

    for (int i = 0; i < steps.getLength(); i++) {

      ResultStep step = new ResultStep();

      String textFromAnnotation = steps.item(i).getTextContent();

      Matcher resultMatcher = resultPattern.matcher(textFromAnnotation);

      if (!resultMatcher.matches() || resultMatcher.groupCount() != 1) {
        throw new Exception("No Result found in Step" + textFromAnnotation
            + " in file " + this.annotationFile.toString());
      }

      StepResult stepResult = StepResult.valueOf(resultMatcher.group(1));
      step.setStepResult(stepResult);

      textFromAnnotation = textFromAnnotation.substring(0,
          textFromAnnotation.length() - stepResult.toString().length() - 1);

      String[] parts = textFromAnnotation.split("\\|\\|");
      step.setStepText(parts[0]);

      // Add Table Rows
      String[] tableRow;
      for (int rowCount = 1; rowCount < parts.length; rowCount++) {
        tableRow = parts[rowCount].split("\\|");
        step.addSetUpDataRow(Arrays.asList(tableRow));
      }

      refSlotStart = steps.item(i).getParentNode().getAttributes()
          .getNamedItem("TIME_SLOT_REF1").getNodeValue();
      refSlotEnd = steps.item(i).getParentNode().getAttributes()
          .getNamedItem("TIME_SLOT_REF2").getNodeValue();

      step.setMillisecondsFrom(timeslots.get(refSlotStart));
      step.setMillisecondsTo(timeslots.get(refSlotEnd));
      stepList.add(step);

    }

    return stepList;

  }

  @Override
  public String parseSha1Checksum() throws Throwable {
    XPath xpath = XPathFactory.newInstance().newXPath();
    XPathExpression expr = xpath
        .compile("//ANNOTATION_DOCUMENT/HEADER/MEDIA_DESCRIPTOR/@MIME_TYPE");
    return (String) expr.evaluate(doc, XPathConstants.STRING);
  }

}
