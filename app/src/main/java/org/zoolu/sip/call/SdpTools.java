
package org.zoolu.sip.call;

import org.zoolu.sdp.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Class SdpTools collects some static methods for managing SDP materials.
 */
public class SdpTools {
	
	/* HSC CHANGES START */
	public static SessionDescriptor sdpMediaProduct(SessionDescriptor sdp,
			Vector<MediaDescriptor> m_descs) {
		Vector<MediaDescriptor> new_media = new Vector<MediaDescriptor>();
		if (m_descs != null) {
			for (Enumeration<MediaDescriptor> e = m_descs.elements(); e
					.hasMoreElements();) {
				MediaDescriptor spec_md = e.nextElement();
				// System.out.print("DEBUG: SDP: sdp_select:
				// "+spec_md.toString());
				MediaDescriptor prev_md = sdp.getMediaDescriptor(spec_md
						.getMedia().getMedia());
				// System.out.print("DEBUG: SDP: sdp_origin:
				// "+prev_md.toString());
				if (prev_md != null) {
					Vector<AttributeField> spec_attributes = spec_md
							.getAttributes();
					Vector<AttributeField> prev_attributes = prev_md
							.getAttributes();
					MediaField prev_mf = prev_md.getMedia();
					Vector<String> new_formats = new Vector<String>(prev_mf.getFormatList());
					new_formats.retainAll(spec_md.getMedia().getFormatList());
					
					if (spec_attributes.size() == 0
							|| prev_attributes.size() == 0) {
						new_media.addElement(prev_md);
					} else {
						Vector<AttributeField> new_attributes = new Vector<AttributeField>();
						for (Enumeration<AttributeField> i = spec_attributes
								.elements(); i.hasMoreElements();) {
							AttributeField spec_attr = i.nextElement();
							String spec_name = spec_attr.getAttributeName();
							String spec_value = spec_attr.getAttributeValue();
							for (Enumeration<AttributeField> k = prev_attributes
									.elements(); k.hasMoreElements();) {
								AttributeField prev_attr = k.nextElement();
								String prev_name = prev_attr.getAttributeName();
								String prev_value = prev_attr
										.getAttributeValue();
								if (prev_name.equals(spec_name)
										&& prev_value
												.equalsIgnoreCase(spec_value)) {
									new_attributes.addElement(prev_attr);
									break;
								}
							}
						}
						MediaField new_mf = new MediaField(prev_mf.getMedia(), prev_mf.getPort(), 0,
								prev_mf.getTransport(), new_formats);
						if (new_attributes.size() > 0)
							new_media.addElement(new MediaDescriptor(new_mf, prev_md.getConnection(),
									new_attributes));
				        else {
			                if(new_mf.getMedia().startsWith("audio") && new_formats.size() > 0) {
			                        new_media.addElement(new MediaDescriptor(new_mf, prev_md.getConnection(),
			                                new_attributes)); // new_attributes is empty but this is ok here.
			                }
				        }
					}
				}
			}
		}
		SessionDescriptor new_sdp = new SessionDescriptor(sdp);
		new_sdp.removeMediaDescriptors();
		new_sdp.addMediaDescriptors(new_media);
		return new_sdp;
	}

	/* HSC CHANGES END */
	/**
	 * Costructs a new SessionDescriptor from a given SessionDescriptor with
	 * olny the first specified media attribute. /** Keeps only the fisrt
	 * attribute of the specified type for each media.
	 * <p>
	 * If no attribute is present for a media, the media is dropped.
	 * 
	 * @param sdp
	 *            the given SessionDescriptor
	 * @param a_name
	 *            the attribute name
	 * @return this SessionDescriptor
	 */
	/* HSC CHANGES START */
	public static SessionDescriptor sdpAttirbuteSelection(
			SessionDescriptor sdp, String a_name) {
		Vector<MediaDescriptor> new_media = new Vector<MediaDescriptor>();
		for (Enumeration<MediaDescriptor> e = sdp.getMediaDescriptors()
				.elements(); e.hasMoreElements();) {
			/* HSC CHANGES END */
			MediaDescriptor md = e.nextElement();
			AttributeField attr = md.getAttribute(a_name);
			if (attr != null) {
				new_media.addElement(new MediaDescriptor(md.getMedia(), md
						.getConnection(), attr));
			}
		}
		SessionDescriptor new_sdp = new SessionDescriptor(sdp);
		new_sdp.removeMediaDescriptors();
		new_sdp.addMediaDescriptors(new_media);
		return new_sdp;
	}

}
