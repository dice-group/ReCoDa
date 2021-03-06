package org.dice_research.opal.licenses.transform;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dice_research.opal.licenses.KnowledgeBase;
import org.dice_research.opal.licenses.KnowledgeBases;
import org.dice_research.opal.licenses.cc.CcData;
import org.dice_research.opal.licenses.utils.Cfg;
import org.dice_research.opal.licenses.utils.CollectionUtil;

/**
 * Configuration: Set CC.LicenseRDF directory using the system property
 * {@link Cfg#KEY_CC_LICENSERDF}. E.g. by VM argument
 * "-Dcc.licenserdf=../cc.licenserdf/cc/licenserdf/licenses/".
 *
 * @author Adrian Wilke
 */
public class KbGenCcChart extends KbGen {

	public static void main(String[] args) throws Exception {
		File file = new KbGenCcChart().export();
		System.out.println("Exported: " + file.getAbsolutePath());
	}

	@Override
	public Collection<String> getAttribueEqualityUris() {
		return CollectionUtil.stringToSet(CcData.SHARE_ALIKE);
	}

	@Override
	public Collection<String> getPermissionOfDerivatesUris() {
		return CollectionUtil.stringToSet(CcData.DERIVATIVE_WORKS);
	}

	@Override
	public String getTitle() {
		return KnowledgeBases.ID_CC_MATRIX;
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		CcData ccData;
		try {
			ccData = new CcData().setSourceDirectory(Cfg.getCcLicenseRdf()).readDirectory();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		List<File> files = ccData.getMatixFiles();
		return ccData.createKnowledgeBase(files);
	}

	@Override
	public Map<String, String> getPrefixes() {
		Map<String, String> prefixes = new HashMap<>();
		return prefixes;
	}

}