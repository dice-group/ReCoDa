package org.dice_research.opal.licenses.old;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class ODRL {

	public static final Model model;

	private enum OptionalBoolean {
		UNDEFINED, FALSE, TRUE
	}

	static {
		model = ModelFactory.createDefaultModel();

		model.setNsPrefix("opal", Constants.NS_OPAL_LICENSES);
		model.setNsPrefix("odrl", Constants.NS_ODRL);
		model.setNsPrefix("cc", Constants.NS_CC);

		try {
			// model.read(new FileInputStream(new File("src/main/resources/ODRL22.ttl")),
			// null, "TURTLE");

			Files.lines(new File("edp-matrix.csv").toPath()).forEach((String line) -> {
				String fields[] = line.split(",");

				// String licenseName = fields[0];
				String licenseURI = fields[1];

				OptionalBoolean bFields[] = new OptionalBoolean[fields.length - 2];

				for (int i = 2; i < fields.length; i++) {
					if ("Yes".equals(fields[i]))
						bFields[i - 2] = OptionalBoolean.TRUE;
					else if ("No".equals(fields[i]))
						bFields[i - 2] = OptionalBoolean.FALSE;
					else
						bFields[i - 2] = OptionalBoolean.UNDEFINED;
				}

				// Permissions
				OptionalBoolean reproduction = bFields[0];
				OptionalBoolean distribution = bFields[1];
				OptionalBoolean derivative = bFields[2];
				// OptionalBoolean sublicensing = bFields[3]; // not supported by ODRL / CC-REL
				// OptionalBoolean patentGrant = bFields[4]; // not supported by ODRL / CC-REL

				// Requirements
				OptionalBoolean notice = bFields[5]; // CC-REL only
				OptionalBoolean attribution = bFields[6]; // CC-REL only
				OptionalBoolean shareAlike = bFields[7]; // CC-REL only
				OptionalBoolean copyleft = bFields[8]; // CC-REL only
				OptionalBoolean lesserCopyleft = bFields[9]; // CC-REL only
				// OptionalBoolean stateChanges = bFields[10]; // not supported by CC-REL / ODRL

				// Prohibitions
				OptionalBoolean commercialUse = bFields[11]; // CC-REL only
				// OptionalBoolean useTrademark = bFields[12]; // not supported by CC-REL

				Resource license = model.createResource(licenseURI);
				license.addProperty(RDF.type, model.createResource(Constants.NS_ODRL + "policy"));

				Property permission = model.createProperty(Constants.NS_ODRL + "permission");
				Resource permissions = model.createResource();
				license.addProperty(permission, permissions);

				Property requirement = model.createProperty(Constants.NS_ODRL + "requirement");
				Resource requirements = model.createResource();
				license.addProperty(requirement, requirements);

				Property prohibition = model.createProperty(Constants.NS_ODRL + "prohibition");
				Resource prohibitions = model.createResource();
				license.addProperty(prohibition, prohibitions);

				Property action = model.createProperty(Constants.NS_ODRL + "action");

				if (reproduction == OptionalBoolean.TRUE)
					permissions.addProperty(action, model.createProperty(Constants.NS_ODRL + "reproduce"));
				if (distribution == OptionalBoolean.TRUE)
					permissions.addProperty(action, model.createProperty(Constants.NS_ODRL + "distribute"));
				if (derivative == OptionalBoolean.TRUE)
					permissions.addProperty(action, model.createProperty(Constants.NS_ODRL + "derive"));

				if (notice == OptionalBoolean.TRUE)
					requirements.addProperty(action, model.createProperty(Constants.NS_CC + "Notice"));
				if (attribution == OptionalBoolean.TRUE)
					requirements.addProperty(action, model.createProperty(Constants.NS_CC + "Attribution"));
				if (shareAlike == OptionalBoolean.TRUE)
					requirements.addProperty(action, model.createProperty(Constants.NS_CC + "ShareAlike"));
				if (copyleft == OptionalBoolean.TRUE)
					requirements.addProperty(action, model.createProperty(Constants.NS_CC + "Copyleft"));
				if (lesserCopyleft == OptionalBoolean.TRUE)
					requirements.addProperty(action, model.createProperty(Constants.NS_CC + "LesserCopyleft"));

				if (commercialUse == OptionalBoolean.TRUE)
					prohibitions.addProperty(action, model.createProperty(Constants.NS_CC + "CommercialUse"));

			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		// try {
		// model.write(new FileOutputStream(new File("licenses.ttl")), "TURTLE");
		model.write(System.out, "TURTLE");
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }
	}
}