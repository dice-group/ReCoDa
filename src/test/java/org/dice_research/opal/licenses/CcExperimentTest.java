package org.dice_research.opal.licenses;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.dice_research.opal.licenses.cc.CcData;
import org.dice_research.opal.licenses.cc.CcMatrix;
import org.dice_research.opal.licenses.utils.ArrayUtil;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests Creative Commons license compatibility.
 * 
 * @see https://wiki.creativecommons.org/index.php?title=Wiki/cc_license_compatibility&oldid=70058
 * 
 * @author Adrian Wilke
 */
public class CcExperimentTest {

	public static final String DATA_DIRECTORY = "../cc.licenserdf/cc/licenserdf/licenses/";
	public static final String DATA_REPOSITORY = "https://github.com/projekt-opal/cc.licenserdf";

	public KnowledgeBase knowledgeBase;
	public CcMatrix matrix;

	/**
	 * Checks data directory. Creates objects.
	 */
	@Before
	public void setUp() {
		Assume.assumeTrue("Please make available: " + DATA_REPOSITORY, new File(DATA_DIRECTORY).exists());
		CcData data = new CcData();
		knowledgeBase = data.setSourceDirectory(DATA_DIRECTORY).createKnowledgeBase(data.getMatixFiles());
		matrix = new CcMatrix();
	}

	/**
	 * Tests if CC matrix results are computed correctly.
	 */
	@Test
	public void testCreativeCommonsCompatibility() {
		boolean status = true;
		StringBuilder stringBuilder = new StringBuilder();

		// Combine licenses to check every cell in matrix
		for (License licenseA : knowledgeBase.getLicenses()) {
			for (License licenseB : knowledgeBase.getLicenses()) {

				List<License> inputLicenses = new ArrayList<>(2);
				inputLicenses.add(licenseA);
				inputLicenses.add(licenseB);

				// Operator used to compute array of internal values
				boolean[] result = new Operator().compute(licenseA.getAttributes().getInternalValuesArray(),
						licenseB.getAttributes().getInternalValuesArray());

				// Back-mapping
				List<License> resultingLicenses = knowledgeBase.getMatchingLicenses(inputLicenses, result);

				// Check license combination and update result status
				status = status & checkResults(licenseA, licenseB, resultingLicenses, stringBuilder);
			}
		}

		// Print debugging info, if test failed
		if (!status) {
			stringBuilder.append("Expected compatibility results:");
			stringBuilder.append(System.lineSeparator());
			stringBuilder.append(matrix);
			stringBuilder.append(System.lineSeparator());
			stringBuilder.append("KnowledgeBase attributes:");
			stringBuilder.append(System.lineSeparator());
			stringBuilder.append(knowledgeBase.toLines());
			System.out.println(stringBuilder.toString());
		}
		Assert.assertTrue("Creative Commons compatibility", status);
	}

	/**
	 * Checks single license. Used in {@link #testCreativeCommonsCompatibility()}.
	 */
	private boolean checkResults(License licenseA, License licenseB, List<License> resultingLicenses,
			StringBuilder stringBuilder) {
		boolean status = true;
		List<String> resultingUris = resultingLicenses.stream().map(l -> l.getUri()).collect(Collectors.toList());
		boolean matrixValue = matrix.getBoolean(licenseA.getUri(), licenseB.getUri());

		// From wiki: 'Use at least the most restrictive licensing of the two'
		License mostRestrictive = licenseA;
		if (matrix.getUris().indexOf(licenseB.getUri()) > matrix.getUris().indexOf(licenseA.getUri())) {
			mostRestrictive = licenseB;
		}

		// Check result and add debugging information if test failed
		if (matrixValue && !resultingUris.contains(mostRestrictive.getUri())) {
			stringBuilder.append("Missing: ");
			stringBuilder.append(ArrayUtil.intString(mostRestrictive.getAttributes().getValuesArray()));
			stringBuilder.append(" ");
			stringBuilder.append(mostRestrictive.getUri());
			stringBuilder.append(System.lineSeparator());
			status = false;
		} else if (!matrixValue && resultingUris.contains(mostRestrictive.getUri())) {
			stringBuilder.append("Wrong:   ");
			stringBuilder.append(ArrayUtil.intString(mostRestrictive.getAttributes().getValuesArray()));
			stringBuilder.append(" ");
			stringBuilder.append(mostRestrictive.getUri());
			stringBuilder.append(System.lineSeparator());
			status = false;
		}

		// Add debugging information if test failed
		if (!status) {
			stringBuilder.append("Checked: ");
			stringBuilder.append(ArrayUtil.intString(licenseA.getAttributes().getValuesArray()));
			stringBuilder.append(" ");
			stringBuilder.append(licenseA.toString());
			stringBuilder.append(System.lineSeparator());
			stringBuilder.append("         ");
			stringBuilder.append(ArrayUtil.intString(licenseB.getAttributes().getValuesArray()));
			stringBuilder.append(" ");
			stringBuilder.append(licenseB.toString());
			stringBuilder.append(System.lineSeparator());
			stringBuilder.append(System.lineSeparator());
		}

		return status;
	}

	/**
	 * Tests if URIs from matrix and from RDF files are equal.
	 */
	@Test
	public void testEqualLicenseUris() {
		Assert.assertTrue(matrix.getUris().containsAll(knowledgeBase.getLicenseUris()));
		Assert.assertTrue(knowledgeBase.getLicenseUris().containsAll(matrix.getUris()));
	}
}