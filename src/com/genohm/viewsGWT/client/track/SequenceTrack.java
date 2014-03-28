/*  
 *   Copyright 2012-2014 Martijn Devisscher
 *
 *   This file is part of boinq.
 *
 *   boinq is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   boinq is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with boinq.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genohm.viewsGWT.client.track;

import org.apache.commons.httpclient.methods.GetMethod;

import com.genohm.viewsGWT.client.ViewPort;
import com.genohm.viewsGWT.client.eventbus.EventBus;
import com.genohm.viewsGWT.client.eventbus.EventbusTopic;
import com.genohm.viewsGWT.client.eventbus.Subscription;
import com.genohm.viewsGWT.client.eventbus.TopicSubscriber;
import com.genohm.viewsGWT.client.renderer.DrawFeature;
import com.genohm.viewsGWT.shared.data.TrackSpecification;
import com.genohm.viewsGWT.shared.renderer.RendererSettings;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.events.MouseMoveEvent;
import com.smartgwt.client.widgets.events.MouseMoveHandler;
import com.smartgwt.client.widgets.events.MouseOutEvent;
import com.smartgwt.client.widgets.events.MouseOutHandler;
import com.smartgwt.client.widgets.events.MouseOverEvent;
import com.smartgwt.client.widgets.events.MouseOverHandler;
import com.smartgwt.client.widgets.layout.SectionStackSection;

public class SequenceTrack extends Track {
	// custom track because it does not have a header and cannot be reordered
	// only loads a single feature containing dnaString of the viewPort
	// also has custom mouseover
	protected HTMLFlow toolTip;
	public static RendererSettings settings = new RendererSettings();
	
//	public SequenceTrack(ViewPort viewPort, String featureSource, RendererSettings rendererSettings) {
//		super(viewPort, "Nucleotide Track", "Track showing reference sequence", featureSource, new RendererSettings("mappedread",500,"green","red",1,0,0.0,0.0,0.0,0.0),null);
//		setShowHeader(false);
//		setCanReorder(false);
//		createTooltip();
//		createPositionInfo();
//	}
	
	public SequenceTrack(ViewPort theViewPort, TrackSpecification trackSpec) {
		super(theViewPort, trackSpec);
		setShowHeader(false);
		setCanReorder(false);
		createTooltip();
		createPositionInfo();
	}
	
//	public SequenceTrack(ViewPort theViewPort, String title,
//			String description, String featureSource,
//			RendererSettings rendererSetting) {
//		super(theViewPort, title, description, featureSource, rendererSetting, null);
//		// TODO Auto-generated constructor stub
//	}

	@Override
	protected void dragMove(Integer offset) {
		if (!MOVING) {
			MOVING = true;
			if (viewPort.getMainGenomicRegion().getWidth() < 50) {
				for (DrawFeature drawFeature: drawFeatures) {
					drawFeature.moveBy(offset, 0);
				}
			}
			else {
				// too heavy to move all features
				drawPane.moveBy(offset, 0);
			}
		}
		MOVING = false;
	}

	protected void createTooltip() {
		toolTip = new HTMLFlow();
		toolTip.setBackgroundColor("lightblue");
		toolTip.setOpacity(80);
		toolTip.setAutoWidth();
		toolTip.setAutoHeight();
		toolTip.setOverflow(Overflow.VISIBLE);
	}
	
	protected void createPositionInfo() {
		final ViewPort theViewPort = this.viewPort;
		drawPane.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				Long position = theViewPort.getMainScaler().getLowerGenomicPosition(event.getX() - drawPane.getAbsoluteLeft());
				toolTip.moveTo(event.getX() + 10,event.getY() + 10);
				toolTip.setContents(position.toString());
				toolTip.show();
			}
		});
		drawPane.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				Long position = theViewPort.getMainScaler().getLowerGenomicPosition(event.getX() - drawPane.getAbsoluteLeft());
				toolTip.moveTo(event.getX() + 10,event.getY() + 10);
				toolTip.setContents(position.toString());
			}
		});
		drawPane.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if (toolTip != null) {
					toolTip.hide();
				}
			}
		});
	}
	
	@Override
	protected void draw() throws Exception {
		// let sequencetrack control the width
		//viewPort.setScreenStart(drawPane.getInnerWidth()/3);
		//viewPort.setScreenEnd(2*drawPane.getInnerWidth()/3);
		viewPort.setScreenStart(0);
		viewPort.setScreenEnd(drawPane.getInnerWidth());
//		viewPort.setScreenStart(drawPane.getAbsoluteLeft());
//		viewPort.setScreenEnd(drawPane.getAbsoluteLeft() + drawPane.getInnerWidth());
		super.draw();
	}

	
}
