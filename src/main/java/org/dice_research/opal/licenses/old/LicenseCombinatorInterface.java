package org.dice_research.opal.licenses.old;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.dice_research.opal.licenses.old.exceptions.UnknownLicenseException;

/**
 * Components implementing this interface create suggestions for possible
 * licenses to be combined. It is based on a collection of input licenses, which
 * are related to restrictions to be met.
 *
 * @author Adrian Wilke
 */
public interface LicenseCombinatorInterface {

	/**
	 * Suggests licenses for a combination of different input licenses.
	 * 
	 * @param usedLicenseUris A collection of license-URIs, which have to be
	 *                        considered for suggesting possible licenses.
	 * 
	 * @return A list of URIs of licenses, which can be used related to restrictions
	 *         of the input licenses. The list is ordered. If it contains licenses
	 *         contained in the input, these should be listed at top. If licenses
	 *         are more probable, e.g. by their openness, they should be listed
	 *         before others. The returned list may be empty.
	 * 
	 * 
	 * @throws UnknownLicenseException If an input-URI is not known.
	 */
	List<String> getLicenseSuggestions(Collection<String> usedLicenseUris) throws UnknownLicenseException;
	
	/**
	 * Computes the attribute set of the smallest denominator of different input licenses.
	 * 
	 * @param usedLicenceUris A non-empty collection of license-URIs, which have to be
	 *                        considered for computing license attributes.
	 *
	 * @return A map containing various license attributes.
	 * 
	 * @throws UnknownLicenseException If an input-URI is not known.
	 * @throws IllegalArgumentException If the input collection is empty
	 */
	Map<LicenseAttribute, Boolean> getLicenseAttributes(Collection<String> usedLicenseUris) throws UnknownLicenseException;
	
	/**
	 * Searches for licenses compatible to the given attributes.
	 * 
	 * @param attributes A map containing various license attributes.
	 * 
	 * @return A list of licenses, which are compatible to the given attributes. May be empty if no matching license could be found.
	 */
	List<String> getLicenseFromAttributes(Map<LicenseAttribute, Boolean> attributes);
	
	/**
	 * Suggests licenses for a combination of different input licenses gathered from 2 Jena models.
	 * 
	 * Licenses are found by DCAT's dct:license property.
	 * Input models should contain one dataset and may contain zero or more distributions of the dataset.
	 * 
	 * @param model0 First input model
	 * @param model1 Second input model
	 * @return A Set of license suggestions
	 * @throws UnknownLicenseException If a model contains some unknown license-URI
	 */
	Collection<List<String>> getLicenseSuggestions(Model model0, Model model1) throws UnknownLicenseException;

}