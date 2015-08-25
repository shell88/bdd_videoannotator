package com.github.shell88.bddvideoannotator.annotationfile.exporter;

import com.github.shell88.bddvideoannotator.annotationfile.exporter.eaf.ANNOTATIONDOCUMENT;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.eaf.AlignableType;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.eaf.AnnotationType;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.eaf.ConstraintType;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.eaf.HeadType;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.eaf.HeadType.MEDIADESCRIPTOR;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.eaf.LingType;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.eaf.PropType;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.eaf.TierType;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.eaf.TimeType;
import com.github.shell88.bddvideoannotator.annotationfile.exporter.eaf.TimeType.TIMESLOT;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Exports Annotations to the EAF-Format.
 * 
 * @author Hell
 */

public class EafAnnotationExporter extends AnnotationExporter {
  /**
   * Head-element for the EAF-XML.
   */
  private ANNOTATIONDOCUMENT eafTree;
  /**
   * Used when new timeslots are added.
   */
  private TimeType timeType;
  private TierType tierSteps;

  /**
   * @param outputDirectory
   *          Target-File for the eaf-annotations.
   */
  public EafAnnotationExporter(File outputDirectory) {
    super(outputDirectory);
  }

  /**
   * Initializes the EAF-output document.
   * @throws DatatypeConfigurationException
   *           Problems while adding calendar information
   */
  private void initializeNewEafTree() throws DatatypeConfigurationException {

    eafTree = new ANNOTATIONDOCUMENT();

    GregorianCalendar gcal = new GregorianCalendar();
    XMLGregorianCalendar xgcal = DatatypeFactory.newInstance()
        .newXMLGregorianCalendar(gcal);

    // initialize Annotationdocument
    eafTree.setAUTHOR("BDDVideoAnnotator");
    eafTree.setDATE(xgcal);
    eafTree.setVERSION("2.7");

    HeadType htype = new HeadType();
    htype.setTIMEUNITS("milliseconds");

    MEDIADESCRIPTOR md = new HeadType.MEDIADESCRIPTOR();
    md.setMEDIAURL("");
    md.setMIMETYPE("video");

    htype.getMEDIADESCRIPTOR().add(md);
    eafTree.setHEADER(htype);

    this.timeType = new TimeType();
    eafTree.setTIMEORDER(this.timeType);

    // Predefined Constraints, that must be contained in an EAF-File
    ConstraintType ctype = new ConstraintType();
    ctype.setDESCRIPTION("Time subdivision of parent "
        + "annotation's time interval");
    ctype.setSTEREOTYPE("Time_Subdivision");
    eafTree.getCONSTRAINT().add(ctype);

    ctype = new ConstraintType();
    ctype.setDESCRIPTION("Symbolic subdivision of a parent annotation");
    ctype.setSTEREOTYPE("Symbolic_Subdivision");
    eafTree.getCONSTRAINT().add(ctype);

    ctype = new ConstraintType();
    ctype.setDESCRIPTION("1-1 association with a parent annotation");
    ctype.setSTEREOTYPE("Symbolic_Association");
    eafTree.getCONSTRAINT().add(ctype);

    ctype = new ConstraintType();
    ctype.setDESCRIPTION("Time alignable annotations within the parent "
        + "annotation's time interval");

    ctype.setSTEREOTYPE("Included_In");
    eafTree.getCONSTRAINT().add(ctype);

    // Tiers
    LingType ltype = new LingType();
    ltype.setLINGUISTICTYPEID("text");
    ltype.setTIMEALIGNABLE(true);
    ltype.setGRAPHICREFERENCES(false);
    eafTree.getLINGUISTICTYPE().add(ltype);

    tierSteps = new TierType();
    tierSteps.setTIERID("Steps");
    tierSteps.setLINGUISTICTYPEREF(ltype.getLINGUISTICTYPEID());
    eafTree.getTIER().add(tierSteps);

  }

  /**
   * Inserts a new TIMESLOT-Element in the xml tree.
   * 
   * @param value
   *          - The time value for the timeslot (milliseconds)
   * @return TIMESLOT - The generated timeslot that can be used for referencing
   */
  private TIMESLOT addNewTimeSlot(Long value) {
    Integer id = this.timeType.getTIMESLOT().size() + 1;
    TIMESLOT ts = new TIMESLOT();
    ts.setTIMESLOTID("ts" + id);
    ts.setTIMEVALUE(value);
    this.eafTree.getTIMEORDER().getTIMESLOT().add(ts);
    return ts;
  }

  /**
   * @param ttype
   *          The tier-element in the XML-tree.
   * @return idAnnotation An unused identifier for that tier that can be used to
   *         add a new annotation
   */
  private synchronized String getNewAnnotationId(TierType ttype) {
    String idAnnotation = "a" + ttype.getANNOTATION().size() + 1;
    return idAnnotation;
  }

  private void addScenarioText(String scenarioText) {
    PropType scenarioProp = new PropType();
    scenarioProp.setNAME("Scenario");
    scenarioProp.setValue(scenarioText);
    eafTree.getHEADER().getPROPERTY().add(scenarioProp);
  }

  private void addVideoReferenceFile(String pathToVideofile, String checksum) {
    eafTree.getHEADER().getMEDIADESCRIPTOR().get(0)
        .setMEDIAURL(pathToVideofile);

    PropType shaProptype = new PropType();
    shaProptype.setNAME("SHA1");
    shaProptype.setValue(checksum);
    eafTree.getHEADER().getPROPERTY().add(shaProptype);
  }

  private void addFeatureText(String featureText) {
    PropType featureProp = new PropType();
    featureProp.setNAME("Feature");
    featureProp.setValue(featureText);
    eafTree.getHEADER().getPROPERTY().add(featureProp);
  }

  private void addStepAnnotations(List<StepAnnotation> stepAnnotations) {
    String idAnnotation;
    for (StepAnnotation stepAnnotation : stepAnnotations) {
      idAnnotation = this.getNewAnnotationId(tierSteps);
      AlignableType type = new AlignableType();
      type.setANNOTATIONID(idAnnotation);
      TIMESLOT startSlot = this.addNewTimeSlot(stepAnnotation
          .getMillisecondsFrom());
      TIMESLOT endSlot = this
          .addNewTimeSlot(stepAnnotation.getMillisecondsTo());
      type.setANNOTATIONVALUE(stepAnnotation.toString());
      type.setTIMESLOTREF1(startSlot);
      type.setTIMESLOTREF2(endSlot);
      AnnotationType ann = new AnnotationType();
      ann.setALIGNABLEANNOTATION(type);
      tierSteps.getANNOTATION().add(ann);
    }
  }

  @Override
  public void write(ScenarioAnnotationsDto exportable) throws IOException {

    try {
      initializeNewEafTree();
    } catch (DatatypeConfigurationException e) {
      throw new IOException("Could not initialize EAF File " + e.getMessage());
    }

    addFeatureText(exportable.getFeatureText());
    addScenarioText(exportable.getScenarioText());
    addStepAnnotations(exportable.getStepAnnotations());
    addVideoReferenceFile(exportable.getNameVideoFile(),
        exportable.getSha1ChecksumVideo());

    String prefix = "annotations.eaf";
    if (!exportable.getScenarioText().isEmpty()) {
      prefix = exportable.getScenarioText();
    }
    prefix = FilenameUtils.normalize(prefix);
    File outputFile = Helper.createNewOutputFile(super.getOutputDirectory(),
        prefix, "eaf");

    /*
     * Validation against xml-schema could be performed by the display software
     * As the annotation file will be used as a test protocol, it should not be
     * abborted due to incompatibility with the xml-schema
     */
    JAXB.marshal(eafTree, outputFile);

  }

}