package org.dice_research.opal.licenses;

import java.util.Arrays;
import java.util.Collection;

import org.dice_research.opal.licenses.operator.Attribute;
import org.dice_research.opal.licenses.operator.Attributes;
import org.dice_research.opal.licenses.operator.EdpKnowledgeBase;
import org.dice_research.opal.licenses.operator.License;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

/**
 * Tests {@link EdpKnowledgeBase}.
 *
 * @author Adrian Wilke
 */
public class EdpKnowledgeBaseTest {

	/**
	 * Config: Execute {@link #print()}.
	 */
	public static final boolean PRINT = true;

	// Does not include attribute 'Sublicensing' as it contains a 'N.A.' value.
	public static final int NUMBER_OF_ATTRIBUTES = 13 - 1;
	public static final int NUMBER_OF_LICENSES = 32;

	@Test
	public void testAttributes() {
		Attributes attributes = new EdpKnowledgeBase().getAttributes();
		checkAttributes(attributes, false);
	}

	@Test
	public void testLicenses() {
		Collection<License> licenses = new EdpKnowledgeBase().getUrisToLicenses().values();
		Assert.assertEquals(NUMBER_OF_LICENSES, licenses.size());
		for (License license : licenses) {
			Assert.assertNotNull(license.getUri());
			Assert.assertFalse(license.getUri().isEmpty());
			Assert.assertNotNull(license.getName());
			Assert.assertFalse(license.getName().isEmpty());
			checkAttributes(license.getAttributes(), true);
		}
	}

	@Test
	public void print() {
		Assume.assumeTrue(PRINT);

		EdpKnowledgeBase edpKnowledgeBase = new EdpKnowledgeBase();

		// Use IDs as URIs (true) or use KB URIs (false)
		if (Boolean.TRUE) {
			edpKnowledgeBase.useIdsAsUris = true;
		}

		Collection<License> licenses = edpKnowledgeBase.getUrisToLicenses().values();
		Attributes attributes = edpKnowledgeBase.getAttributes();
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(getClass().getName());
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append(attributes.toLines());
		stringBuilder.append(System.lineSeparator());

		for (License license : licenses) {
			stringBuilder.append(license);
			stringBuilder.append(System.lineSeparator());
			stringBuilder.append("Values:   ");
			stringBuilder.append(Arrays.toString(license.getAttributes().getValuesArray()));
			stringBuilder.append(System.lineSeparator());
			stringBuilder.append("Internal: ");
			stringBuilder.append(Arrays.toString(license.getAttributes().getInternalArray()));
			stringBuilder.append(System.lineSeparator());
			stringBuilder.append(System.lineSeparator());
		}

		System.out.println(stringBuilder.toString());
	}

	protected void checkAttributes(Attributes attributes, boolean hasValue) {

		Assert.assertEquals(NUMBER_OF_ATTRIBUTES, attributes.getUriToAttributeMap().size());

		// Keys
		for (String key : attributes.getUris()) {
			Assert.assertNotNull(key);
			Assert.assertFalse(key.isEmpty());
		}

		// Values
		for (Attribute attribute : attributes.getObjects()) {
			Assert.assertNotNull(attribute.getUri());
			Assert.assertFalse(attribute.getUri().isEmpty());
			if (hasValue) {
				Assert.assertTrue(attribute.hasValue());
				Assert.assertNotNull(attribute.getUri());
				Assert.assertFalse(attribute.getUri().isEmpty());
			} else {
				Assert.assertFalse(attribute.hasValue());
			}
		}
	}
}