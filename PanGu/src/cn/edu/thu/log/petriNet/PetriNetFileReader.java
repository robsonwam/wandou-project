package cn.edu.thu.log.petriNet;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.Pnml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
/**
 * get PetriNet from .PNML file
 * @author meng
 *
 */
public class PetriNetFileReader {
	private File file = null;

	public PetriNetFileReader() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param f
	 * @return objects; objects[0] = net; objects[1] = marking;
	 * 	 * @throws Exception
	 */
	public Object importFile(File f) throws Exception {
		file = f;
		InputStream stream = new FileInputStream(file);
		return importFromStream(stream, file.getName(), file.length());
	}

	private Object importFromStream(InputStream input, String filename,
			long fileSizeInBytes) throws Exception {
		// PnmlImportUtils utils = new PnmlImportUtils();
		Pnml pnml = importPnmlFromStream(input, filename, fileSizeInBytes);
		if (pnml == null) {
			/*
			 * No PNML found in file. Fail.
			 */
			return null;
		}
		/*
		 * PNML file has been imported. Now we need to convert the contents to a
		 * regular Petri net.
		 */
		PetrinetGraph net = PetrinetFactory.newPetrinet(pnml.getLabel()
				+ " (imported from " + filename + ")");

		return connectNet(pnml, net);
	}

	private Pnml importPnmlFromStream(InputStream input, String filename,
			long fileSizeInBytes) throws Exception {
		/*
		 * Get an XML pull parser.
		 */
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		/*
		 * Initialize the parser on the provided input.
		 */
		xpp.setInput(input, null);
		/*
		 * Get the first event type.
		 */
		int eventType = xpp.getEventType();
		/*
		 * Create a fresh PNML object.
		 */
		Pnml pnml = new Pnml();

		/*
		 * Skip whatever we find until we've found a start tag.
		 */
		while (eventType != XmlPullParser.START_TAG) {
			eventType = xpp.next();
		}
		/*
		 * Check whether start tag corresponds to PNML start tag.
		 */
		if (xpp.getName().equals(Pnml.TAG)) {
			/*
			 * Yes it does. Import the PNML element.
			 */
			pnml.importElement(xpp, pnml);
		} else {
			/*
			 * No it does not. Return null to signal failure.
			 */
			pnml.log(Pnml.TAG, xpp.getLineNumber(), "Expected pnml");
		}
		if (pnml.hasErrors()) {
			// context.getProvidedObjectManager().createProvidedObject("Log of PNML import",
			// pnml.getLog(), XLog.class,
			// context);
			System.out.println("phml.hasError()");
			return null;
		}
		return pnml;
	}

	private Object connectNet(Pnml pnml, PetrinetGraph net) {
		/*
		 * Create a fresh marking.
		 */
		Marking marking = new Marking();

		GraphLayoutConnection layout = new GraphLayoutConnection(net);
		/*
		 * Initialize the Petri net and marking from the PNML element.
		 */
		pnml.convertToNet(net, marking, layout);

		/*
		 * Add a connection from the Petri net to the marking.
		 */
		// context.addConnection(new InitialMarkingConnection(net, marking));
		// context.addConnection(layout);

		/*
		 * Set the label of the Petri net.
		 */
		// context.getFutureResult(0).setLabel(net.getLabel());
		/*
		 * set the label of the marking.
		 */
		// context.getFutureResult(1).setLabel("Marking of " + net.getLabel());

		/*
		 * Return the net and the marking.
		 */
		Object[] objects = new Object[2];
		objects[0] = net;
		objects[1] = marking;
		return objects;
	}
}
