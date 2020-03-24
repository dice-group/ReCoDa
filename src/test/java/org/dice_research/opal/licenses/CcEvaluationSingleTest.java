package org.dice_research.opal.licenses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dice_research.opal.licenses.cc.CcMatrix;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests single pair of Creative Commons license compatibility.
 * 
 * @see https://wiki.creativecommons.org/index.php?title=Wiki/cc_license_compatibility&oldid=70058
 * 
 * @author Adrian Wilke
 */
public class CcEvaluationSingleTest {

	private KnowledgeBase knowledgeBase;
	private CcMatrix matrix;

	/**
	 * Checks data directory. Creates objects.
	 */
	@Before
	public void setUp() {
		knowledgeBase = CcTestUtils.getKnowledgeBase();
		matrix = new CcMatrix();
	}

	@Test
	public void test_SA_NC() {
		String licenseUriA = CcMatrix.I3_BY_SA;
		String licenseUriB = CcMatrix.I4_BY_NC;

		licenseUriA = CcMatrix.I0_MARK;
		licenseUriB = CcMatrix.I2_BY;

		check(licenseUriA, licenseUriB);
	}

	/**
	 * Tests if CC matrix results are computed correctly.
	 */
	public void check(String licenseUriA, String licenseUriB) {
		boolean status = true;

		System.out.println(licenseUriA);
		System.out.println(
				Arrays.toString(knowledgeBase.getLicense(licenseUriA).getAttributes().getInternalValuesArray()));
		System.out.println(licenseUriB);
		System.out.println(
				Arrays.toString(knowledgeBase.getLicense(licenseUriB).getAttributes().getInternalValuesArray()));
		System.out.println();

		StringBuilder stringBuilder = new StringBuilder();
		List<License> resultingLicenses = new ArrayList<>(0);

		// Combine licenses to check every cell in matrix
		for (License licenseA : knowledgeBase.getLicenses()) {

			if (!licenseA.getUri().equals(licenseUriA))
				continue;

			for (License licenseB : knowledgeBase.getLicenses()) {

				if (!licenseB.getUri().equals(licenseUriB))
					continue;

				List<License> inputLicenses = new ArrayList<>(2);
				inputLicenses.add(licenseA);
				inputLicenses.add(licenseB);

				// Operator used to compute array of internal values
				Execution execution = new Execution().setKnowledgeBase(knowledgeBase);
				Attributes resultAttributes = execution.applyOperator(inputLicenses);
//				boolean[] result = resultAttributes.getInternalValuesArray();O

				// Back-mapping
				resultingLicenses = new BackMapping().getCompatibleLicenses(inputLicenses, resultAttributes,
						knowledgeBase);

				// Check license combination and update result status
				status = status
						& CcEvaluationTest.checkResults(licenseA, licenseB, resultingLicenses, matrix, stringBuilder);
			}
		}

		// Print
		System.out.println("Resulting licenses: ");
		for (License resultingLicense : resultingLicenses) {
			System.out.println(resultingLicense);
		}
		System.out.println();
		System.out.println();

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

}