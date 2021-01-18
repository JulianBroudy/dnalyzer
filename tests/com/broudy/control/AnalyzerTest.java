package com.broudy.control;

import com.broudy.entity.AnalysisInformation;
import com.broudy.entity.AnalysisParameters;
import com.broudy.entity.AutocorrelationArrays;
import com.broudy.entity.FileMetadata;
import com.broudy.entity.Protonav;
import com.broudy.entity.ProtonavPair;
import com.broudy.entity.ProtonavProbabilities;
import com.broudy.entity.Sequence;
import com.broudy.entity.Sequence.SequenceSide;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;

public class AnalyzerTest extends TestCase {

  private Analyzer analyzer;

  public void setUp() throws Exception {
    super.setUp();
    FileMetadata metadata = new FileMetadata("test", "test", "test");
    Sequence leftSequence = new Sequence(SequenceSide.LEFT, "ACGTACGT");
    Sequence rightSequence = new Sequence(SequenceSide.RIGHT, "ACGTACGT");
    AnalysisParameters parameters = new AnalysisParameters(true, 3, 5, 1, 3, 100, 500);
    AnalysisInformation analysisInformation = new AnalysisInformation(metadata, leftSequence,
        rightSequence, parameters);
    analyzer = new Analyzer(analysisInformation);
    analyzer.probabilitiesOfSinglesOnLeft = analysisInformation.getLeftSequence()
        .getNucleotideProbabilities().getProbabilitiesOfSingles();
    analyzer.probabilitiesOfSinglesOnRight = analysisInformation.getRightSequence()
        .getNucleotideProbabilities().getProbabilitiesOfSingles();
    analyzer.totalNumberOfNucleotidesOnLeft = analysisInformation.getLeftSequence()
        .getNucleotideProbabilities().getTotalNumberOfNucleotides();
    analyzer.totalNumberOfNucleotidesOnRight = analysisInformation.getRightSequence()
        .getNucleotideProbabilities().getTotalNumberOfNucleotides();
    analyzer.probabilitiesByPairsOnLeft = analysisInformation.getLeftSequence()
        .getNucleotideProbabilities().getProbabilitiesOfPairs();
    analyzer.probabilitiesByPairsOnRight = analysisInformation.getRightSequence()
        .getNucleotideProbabilities().getProbabilitiesOfPairs();
  }

  public void testGenerateProtonavPair() {
    Protonav extractedProtonav = new Protonav("ACT", new ProtonavProbabilities(0, 0),
        new AutocorrelationArrays(100));
    Protonav palimentaryProtonav = new Protonav("AGT", new ProtonavProbabilities(0, 0),
        new AutocorrelationArrays(100));
    ProtonavPair expectedResult = new ProtonavPair(1, extractedProtonav, palimentaryProtonav);
    ProtonavPair actualResult = analyzer.generateProtonavPair("ACT", 1, 100);

    assertEquals(expectedResult.getExtractedProtonav().getPattern(),
        actualResult.getExtractedProtonav().getPattern());
    assertEquals(expectedResult.getPalimentaryProtonav().getPattern(),
        actualResult.getPalimentaryProtonav().getPattern());
    assertEquals(expectedResult.getID(), actualResult.getID());
  }

  public void testGetComplementaryNucleotide() {
    char expectedResult = 'A';
    char actualResult = analyzer.getComplementaryNucleotide('T');
    assertEquals(expectedResult, actualResult);

    expectedResult = 'T';
    actualResult = analyzer.getComplementaryNucleotide('A');
    assertEquals(expectedResult, actualResult);

    expectedResult = 'C';
    actualResult = analyzer.getComplementaryNucleotide('G');
    assertEquals(expectedResult, actualResult);

    expectedResult = 'G';
    actualResult = analyzer.getComplementaryNucleotide('C');
    assertEquals(expectedResult, actualResult);
  }

  public void testAllLexicographicRecur() {
    List<String> expectedResult = new ArrayList<>(Arrays.asList("A", "C", "G", "T"));
    analyzer.allLexicographic("ACGT", 1);
    assertEquals(expectedResult, analyzer.possiblePatterns);

    expectedResult.addAll(Arrays
        .asList("AA", "AC", "AG", "AT", "CA", "CC", "CG", "CT", "GA", "GC", "GG", "GT", "TA", "TC",
            "TG", "TT"));

    analyzer.allLexicographic("ACGT", 2);
    assertEquals(expectedResult, analyzer.possiblePatterns);
  }


}