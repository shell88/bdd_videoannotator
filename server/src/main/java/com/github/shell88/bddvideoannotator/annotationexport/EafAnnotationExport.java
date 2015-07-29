package com.github.shell88.bddvideoannotator.annotationexport;

import com.github.shell88.bddvideoannotator.eaf.ANNOTATIONDOCUMENT;
import com.github.shell88.bddvideoannotator.eaf.AlignableType;
import com.github.shell88.bddvideoannotator.eaf.AnnotationType;
import com.github.shell88.bddvideoannotator.eaf.ConstraintType;
import com.github.shell88.bddvideoannotator.eaf.HeadType;
import com.github.shell88.bddvideoannotator.eaf.HeadType.MEDIADESCRIPTOR;
import com.github.shell88.bddvideoannotator.eaf.LingType;
import com.github.shell88.bddvideoannotator.eaf.TierType;
import com.github.shell88.bddvideoannotator.eaf.TimeType;
import com.github.shell88.bddvideoannotator.eaf.TimeType.TIMESLOT;
import com.github.shell88.bddvideoannotator.service.Helper;

import java.io.File;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Exports Annotations to the EAF-Format.
 * @author Hell
 */

public class EafAnnotationExport extends AnnotationExporter {
  /**  Head-element for the EAF-XML. 
   */
  private ANNOTATIONDOCUMENT doc;
  /**
   * Used when new timeslots are added.
   */
  private TimeType timeType;

  /**
   * @param output Target-File for the eaf-annotations.
   * @param tiers  Array of Strings that identifies the tiers, for which
   *               annotations will be added to the file.
   * @throws DatatypeConfigurationException
   *               thrown if initialization of EAF-File fails
   */
  public EafAnnotationExport(File outputDirectory, String[] tiers)
      throws DatatypeConfigurationException {
    super(outputDirectory);
    this.initializeEafFile(tiers);
  }
  
  /**
   * Initializes the EAF-output document.
   * @param tiers Tiers for the output document
   * @throws DatatypeConfigurationException 
   * Problems while adding calendar information 
   */
  private void initializeEafFile(String[] tiers)
      throws DatatypeConfigurationException {

    doc = new ANNOTATIONDOCUMENT();

    GregorianCalendar gcal = new GregorianCalendar();
    XMLGregorianCalendar xgcal = DatatypeFactory.newInstance()
        .newXMLGregorianCalendar(gcal);

    // initialize Annotationdocument
    doc.setAUTHOR("BDDVideoAnnotator");
    doc.setDATE(xgcal);
    doc.setVERSION("2.7");

    HeadType htype = new HeadType();
    htype.setTIMEUNITS("milliseconds");

    MEDIADESCRIPTOR md = new HeadType.MEDIADESCRIPTOR();
    md.setMEDIAURL("");
    md.setMIMETYPE("video");

    htype.getMEDIADESCRIPTOR().add(md);
    doc.setHEADER(htype);

    this.timeType = new TimeType();
    doc.setTIMEORDER(this.timeType);

    // Predefined Constraints, that must be contained in an EAF-File
    ConstraintType ctype = new ConstraintType();
    ctype.setDESCRIPTION("Time subdivision of parent " 
                       + "annotation's time interval");
    ctype.setSTEREOTYPE("Time_Subdivision");
    doc.getCONSTRAINT().add(ctype);

    ctype = new ConstraintType();
    ctype.setDESCRIPTION("Symbolic subdivision of a parent annotation");
    ctype.setSTEREOTYPE("Symbolic_Subdivision");
    doc.getCONSTRAINT().add(ctype);

    ctype = new ConstraintType();
    ctype.setDESCRIPTION("1-1 association with a parent annotation");
    ctype.setSTEREOTYPE("Symbolic_Association");
    doc.getCONSTRAINT().add(ctype);

    ctype = new ConstraintType();
    ctype.setDESCRIPTION("Time alignable annotations within the parent "
                        + "annotation's time interval");
    
    ctype.setSTEREOTYPE("Included_In");
    doc.getCONSTRAINT().add(ctype);

    // Tiers
    LingType ltype = new LingType();
    ltype.setLINGUISTICTYPEID("text");
    ltype.setTIMEALIGNABLE("true");
    ltype.setGRAPHICREFERENCES("false");
    doc.getLINGUISTICTYPE().add(ltype);

    for (byte i = 0; i < tiers.length; i++) {
      TierType ttype = new TierType();
      ttype.setTIERID(tiers[i]);
      ttype.setLINGUISTICTYPEREF(ltype);
      doc.getTIER().add(ttype);
    }

  }

  /**
   * @param id   String identifier for the tier.
   * @return     TierType in the XML-Tree
   */
  
  private TierType getTierbyId(String id) {
    TierType ttype = null;
    Iterator<TierType> ttypeIt = this.doc.getTIER().iterator();

    while (ttypeIt.hasNext()) {
      ttype = ttypeIt.next();

      if (ttype.getTIERID().equals(id)) {
        break;
      }

    }

    return ttype;

  }

  /**
   * Inserts a new TIMESLOT-Element in the xml tree.
   * @param value     - The time value for the timeslot (milliseconds)
   * @return TIMESLOT - The generated timeslot that can be used for referencing
   */
  private TIMESLOT addNewTimeSlot(Long value) {
    Integer id = this.timeType.getTIMESLOT().size() + 1;
    TIMESLOT ts = new TIMESLOT();
    ts.setTIMESLOTID("ts" + id);
    ts.setTIMEVALUE(value);
    this.doc.getTIMEORDER().getTIMESLOT().add(ts);
    return ts;
  }

  /**
   * @param  ttype         The tier-element in the XML-tree.
   * @return idAnnotation  An unused identifier for that tier that
   *                       can be used to add a new annotation
   */
  private synchronized String getNewAnnotationId(TierType ttype) {
    String idAnnotation = "a" + ttype.getANNOTATION().size() + 1;
    return idAnnotation;
  }

  @Override
  public void setVideoReferenceFile(String pathToVideofile,
      String checksum) {
    doc.getHEADER().getMEDIADESCRIPTOR().get(0).setMEDIAURL(pathToVideofile);
    doc.getHEADER().getMEDIADESCRIPTOR().get(0).setMIMETYPE(checksum);
  }

  @Override
  public void addStepAnnotation(StepAnnotation step) {
    // TODO Auto-generated method stub

    String annotationText = step.getSteptext()
        + Helper.stringifyDatatable(step.getDataTables()) + " "
        + step.getStepResult().toString();

    addTextualAnnotation("Steps", step.getMillisecondsFrom(),
        step.getMillisecondsTo(), annotationText);

  }
  

  protected void addTextualAnnotation( String tierIdentifier, 
       Long millisFrom,  Long millisTo,  String text) {
    TierType ttype = this.getTierbyId(tierIdentifier);
    String idAnnotation = this.getNewAnnotationId(ttype);

    AlignableType type = new AlignableType();
    type.setANNOTATIONID(idAnnotation);
    TIMESLOT startSlot = this.addNewTimeSlot(millisFrom);
    TIMESLOT endSlot = this.addNewTimeSlot(millisTo);

    type.setANNOTATIONVALUE(text);
    type.setTIMESLOTREF1(startSlot);
    type.setTIMESLOTREF2(endSlot);

    AnnotationType ann = new AnnotationType();
    ann.setALIGNABLEANNOTATION(type);

    ttype.getANNOTATION().add(ann);
  }
  
  @Override
  public void endOfCurrentScenario(String currentScenarioName) throws Exception {

    
    String prefix = "annotations";
    
    if (currentScenarioName != "") {
      // Trim to valid fileName
      prefix = currentScenarioName;
      prefix = prefix.replaceAll("[^a-zA-Z0-9.-]", "_");
      prefix = prefix.replaceAll("\\s", "_");
    }

    File outputFile = Helper.createNewOutputFile(this.getOutputDirectory(),
        prefix, "eaf");
    
    /*
     * Validation against xml-schema could be performed by the display software
     * As the annotation file will be used as a test protocol, it should not be
     * abborted due to incompatibility with the xml-schema
     */
    
    JAXB.marshal(doc, outputFile);
  }


}